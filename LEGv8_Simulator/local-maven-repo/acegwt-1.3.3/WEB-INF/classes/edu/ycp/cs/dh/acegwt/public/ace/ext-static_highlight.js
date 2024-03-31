define("ace/ext/static_highlight",["require","exports","module","ace/edit_session","ace/layer/text","ace/config","ace/lib/dom"], function(require, exports, module) {
"use strict";

var EditSession = require("../edit_session").EditSession;
var TextLayer = require("../layer/text").Text;
var baseStyles = ".ace_static_highlight {\
font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', 'Droid Sans Mono', monospace;\
font-size: 12px;\
white-space: pre-wrap\
}\
.ace_static_highlight .ace_gutter {\
width: 2em;\
text-align: right;\
padding: 0 3px 0 0;\
margin-right: 3px;\
}\
.ace_static_highlight.ace_show_gutter .ace_line {\
padding-left: 2.6em;\
}\
.ace_static_highlight .ace_line { position: relative; }\
.ace_static_highlight .ace_gutter-cell {\
-moz-user-select: -moz-none;\
-khtml-user-select: none;\
-webkit-user-select: none;\
user-select: none;\
top: 0;\
bottom: 0;\
left: 0;\
position: absolute;\
}\
.ace_static_highlight .ace_gutter-cell:before {\
content: counter(ace_line, decimal);\
counter-increment: ace_line;\
}\
.ace_static_highlight {\
counter-reset: ace_line;\
}\
";
var config = require("../config");
var dom = require("../lib/dom");

var simpleDom = {
    createTextNode: function(textContent) {
        return textContent;
    },
    createElement: function(type) {
        var element = {
            type: type,
            style: {},
            childNodes: [],
            appendChild: function(child) {
                element.childNodes.push(child);
            },
            toString: function() {
                var internal = {
                    type: 1,
                    style: 1,
                    className: 1,
                    textContent: 1,
                    childNodes: 1,
                    appendChild: 1,
                    toString: 1
                };
                var stringBuilder = [];
                
                if (element.type != "fragment") {
                    stringBuilder.push("<", element.type);
                    if (element.className)
                        stringBuilder.push(" class='", element.className, "'");
                    var styleStr = [];
                    for (var key in element.style) {
                        styleStr.push(key, ":", element.style[key]);
                    }
                    if (styleStr.length)
                        stringBuilder.push(" style='", styleStr.join(""), "'");
                    for (var key in element) {
                        if (!internal[key]) {
                            stringBuilder.push(" ", key, "='", element[key], "'");
                        }
                    }
                    stringBuilder.push(">");
                }
                
                if (element.textContent) {
                    stringBuilder.push(element.textContent);
                } else {
                    for (var i=0; i<element.childNodes.length; i++) {
                        var child = element.childNodes[i];
                        if (typeof child == "string")
                            stringBuilder.push(child);
                        else
                            stringBuilder.push(child.toString());
                    }
                }
                
                if (element.type != "fragment") {
                    stringBuilder.push("</", element.type, ">");
                }
                
                return stringBuilder.join("");
            }
        };
        return element;
    },
    createFragment: function() {
        return this.createElement("fragment");
    }
};

var SimpleTextLayer = function() {
    this.config = {};
    this.dom = simpleDom;
};
SimpleTextLayer.prototype = TextLayer.prototype;

var highlight = function(el, opts, callback) {
    var m = el.className.match(/lang-(\w+)/);
    var mode = opts.mode || m && ("ace/mode/" + m[1]);
    if (!mode)
        return false;
    var theme = opts.theme || "ace/theme/textmate";
    
    var data = "";
    var nodes = [];

    if (el.firstElementChild) {
        var textLen = 0;
        for (var i = 0; i < el.childNodes.length; i++) {
            var ch = el.childNodes[i];
            if (ch.nodeType == 3) {
                textLen += ch.data.length;
                data += ch.data;
            } else {
                nodes.push(textLen, ch);
            }
        }
    } else {
        data = el.textContent;
        if (opts.trim)
            data = data.trim();
    }
    
    highlight.render(data, mode, theme, opts.firstLineNumber, !opts.showGutter, function (highlighted) {
        dom.importCssString(highlighted.css, "ace_highlight");
        el.innerHTML = highlighted.html;
        var container = el.firstChild.firstChild;
        for (var i = 0; i < nodes.length; i += 2) {
            var pos = highlighted.session.doc.indexToPosition(nodes[i]);
            var node = nodes[i + 1];
            var lineEl = container.children[pos.row];
            lineEl && lineEl.appendChild(node);
        }
        callback && callback();
    });
};
highlight.render = function(input, mode, theme, lineStart, disableGutter, callback) {
    var waiting = 1;
    var modeCache = EditSession.prototype.$modes;
    if (typeof theme == "string") {
        waiting++;
        config.loadModule(['theme', theme], function(m) {
            theme = m;
            --waiting || done();
        });
    }
    var modeOptions;
    if (mode && typeof mode === "object" && !mode.getTokenizer) {
        modeOptions = mode;
        mode = modeOptions.path;
    }
    if (typeof mode == "string") {
        waiting++;
        config.loadModule(['mode', mode], function(m) {
            if (!modeCache[mode] || modeOptions)
                modeCache[mode] = new m.Mode(modeOptions);
            mode = modeCache[mode];
            --waiting || done();
        });
    }
    function done() {
        var result = highlight.renderSync(input, mode, theme, lineStart, disableGutter);
        return callback ? callback(result) : result;
    }
    return --waiting || done();
};
highlight.renderSync = function(input, mode, theme, lineStart, disableGutter) {
    lineStart = parseInt(lineStart || 1, 10);

    var session = new EditSession("");
    session.setUseWorker(false);
    session.setMode(mode);

    var textLayer = new SimpleTextLayer();
    textLayer.setSession(session);

    session.setValue(input);
    var length =  session.getLength();
    
    var outerEl = simpleDom.createElement("div");
    outerEl.className = theme.cssClass;
    
    var innerEl = simpleDom.createElement("div");
    innerEl.className = "ace_static_highlight" + (disableGutter ? "" : " ace_show_gutter");
    innerEl.style["counter-reset"] = "ace_line " + (lineStart - 1);
    outerEl.appendChild(innerEl);

    for (var ix = 0; ix < length; ix++) {
        var lineEl = simpleDom.createElement("div");
        lineEl.className = "ace_line";
        
        if (!disableGutter) {
            var gutterEl = simpleDom.createElement("span");
            gutterEl.className ="ace_gutter ace_gutter-cell";
            gutterEl.unselectable ="on";
            gutterEl.textContent = "";
            lineEl.appendChild(gutterEl);
        }
        textLayer.$renderLine(lineEl, ix, false);
        innerEl.appendChild(lineEl);
    }
    textLayer.destroy();

    return {
        css: baseStyles + theme.cssText,
        html: outerEl.toString(),
        session: session
    };
};

module.exports = highlight;
module.exports.highlight = highlight;
});
                (function() {
                    window.require(["ace/ext/static_highlight"], function(m) {
                        if (typeof module == "object" && typeof exports == "object" && module) {
                            module.exports = m;
                        }
                    });
                })();
            
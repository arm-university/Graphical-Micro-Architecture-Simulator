package com.arm.legv8simulator.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * <code>DatapathGraphics</code> is a graphics library for drawing schematic diagrams on an HTML5 canvas.
 * <p>
 * Wire segments and arrows should always be drawn head to tail to guarantee perfect joins.
 * 
 * @author Jonathan Wright, 2016
 */
public class DatapathGraphics {

	public static final CssColor RED = CssColor.make(255, 77, 77);
	public static final CssColor BLACK = CssColor.make("black");
	public static final CssColor WHITE = CssColor.make("white");
	public static final CssColor GREY = CssColor.make(242,242,242);
	public static final CssColor CONTROL_BLUE = CssColor.make(0, 176, 240);
	public static final CssColor ARM_BLUE = CssColor.make(18, 140, 171);
	
	/**
	 * Draws a diagonal slash to show binary digit length increases for sign extension and zero padding.
	 * 
	 * @param ctx	the context of the canvas on which the slash is to be drawn
	 * @param x		the x-coordinate of the middle of this slash
	 * @param y		the y-coordinate of the middle of this slash
	 * @param color	the color of this slash
	 */
	public static void drawDiagSlash(Context2d ctx, double x, double y, CssColor color) {
		ctx.setStrokeStyle(color);
		ctx.setLineWidth(1.5);
		ctx.beginPath();
		ctx.moveTo(x-5, y-5);
		ctx.lineTo(x+6, y+7);
		ctx.stroke();
		ctx.closePath();
	}
	
	/**
	 * Draws an AND gate with white fill and the outline color specified.
	 * 
	 * @param ctx		the context of the canvas on which the AND gate is to be drawn
	 * @param x			the x-coordinate of the top left corner of the AND gate
	 * @param y			the y-coordinate of the top left corner of the AND gate
	 * @param width		the width of the AND gate in pixels
	 * @param height	the height of the AND gate in pixels
	 * @param color		the outline color of the AND gate
	 */
	public static void drawAndGateHorizontal(Context2d ctx, double x, double y, 
			double width, double height, CssColor color) {
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(WHITE);
		ctx.setLineWidth(2);
		ctx.beginPath();
		ctx.moveTo(x+width/2.0, y);
		ctx.lineTo(x, y);
		ctx.lineTo(x, y+height);
		ctx.lineTo(x+width/2, y+height);
		ctx.arc(x+(width/2), y+height/2, height/2, Math.PI/2, -Math.PI/2, true);
		ctx.fill();
		ctx.stroke();
		ctx.closePath();
	}
	
	/**
	 * Draws an AND gate with white fill and the outline color specified.
	 * 
	 * @param ctx		the context of the canvas on which the AND gate is to be drawn
	 * @param x			the x-coordinate of the top left corner of the AND gate
	 * @param y			the y-coordinate of the top left corner of the AND gate
	 * @param width		the width of the AND gate in pixels
	 * @param height	the height of the AND gate in pixels
	 * @param color		the outline color of the AND gate
	 */
	public static void drawAndGateVertical(Context2d ctx, double x, double y, 
			double width, double height, CssColor color) {
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(WHITE);
		ctx.setLineWidth(2);
		ctx.beginPath();
		ctx.moveTo(x, y+height/2);
		ctx.arc(x+(width/2), y+height/2, width/2, Math.PI, 0, false);
		ctx.moveTo(x+width, y+height/2);
		ctx.lineTo(x+width, y+height);
		ctx.lineTo(x, y+height);
		ctx.lineTo(x, y+height/2);
		ctx.fill();
		ctx.stroke();
		ctx.closePath();
	}
	
	/**
	 * Draws an OR gate with white fill and the outline color specified.
	 * 
	 * @param ctx		the context of the canvas on which the OR gate is to be drawn
	 * @param x			the x-coordinate of the top left corner of the OR gate
	 * @param y			the y-coordinate of the top left corner of the OR gate
	 * @param width		the width of the OR gate in pixels
	 * @param height	the height of the OR gate in pixels
	 * @param color		the outline color of the OR gate
	 */
	public static void drawOrGateHorizontal(Context2d ctx, double x, double y, 
			double width, double height, CssColor color) {
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(WHITE);
		ctx.setLineWidth(2);
		ctx.beginPath();
        ctx.moveTo(x, y);
        ctx.lineTo(x+width/4, y);
        ctx.arc(x+width/4, y+height, height, 3*Math.PI/2, -Math.PI/6, false);
        ctx.arc(x+width/4, y, height, Math.PI/6, Math.PI/2, false);
        ctx.lineTo(x, y+height);
        ctx.arc(x-(Math.sqrt(3)*height)/2, y+height/2, height, Math.PI/6, -Math.PI/6, true);
        ctx.fill();
		ctx.stroke();
		ctx.closePath();
	}
	
	/**
	 * Draws an OR gate with white fill and the outline color specified.
	 * 
	 * @param ctx		the context of the canvas on which the OR gate is to be drawn
	 * @param x			the x-coordinate of the top left corner of the OR gate
	 * @param y			the y-coordinate of the top left corner of the OR gate
	 * @param width		the width of the OR gate in pixels
	 * @param height	the height of the OR gate in pixels
	 * @param color		the outline color of the OR gate
	 */
	public static void drawOrGateVertical(Context2d ctx, double x, double y, 
			double width, double height, CssColor color) {
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(WHITE);
		ctx.setLineWidth(2);
		ctx.beginPath();
		ctx.arc(x, y+3*height/4, width, 0, -Math.PI/3, true);
        ctx.arc(x+width, y+3*height/4, width, -2*Math.PI/3, Math.PI, true);
        ctx.moveTo(x, y+3*height/4);
        ctx.lineTo(x, y+height);
        ctx.arc(x+width/2, y+height+(Math.sqrt(3)*width)/2, width, -2*Math.PI/3, -Math.PI/3, false);
        ctx.lineTo(x+width, y+3*height/4);
        ctx.fill();
		ctx.stroke();
		ctx.closePath();
	}
	
	/**
	 * Draws a rectangle with the standard component colour scheme; black outline, light grey fill with optional red fill.
	 * 
	 * @param ctx				the context of teh canvas on which the rectangle is to be drawn
	 * @param x					the x-coordinate of the top left corner of the rectangle
	 * @param y					the y-coordinate of the top left corner of the rectangle
	 * @param width				the width of the rectangle in pixels
	 * @param height			the height of the rectangle in pixels
	 * @param highlightLeft		whether the left half of the rectangle should be filled red to denote the component being written
	 * @param highlightRight	whether the right half of the rectangle should be filled red to denote the component being read
	 */
	public static void drawCompRect(Context2d ctx, double x, double y, 
			double width, double height, boolean highlightLeft, boolean highlightRight) {
		ctx.setStrokeStyle(BLACK);
		ctx.setLineWidth(2);
		if (highlightLeft) {
			ctx.setFillStyle(RED);
		} else {
			ctx.setFillStyle(GREY);
		}
		ctx.fillRect(x-0.5, y-0.5, width/2, height);
		if (highlightRight) {
			ctx.setFillStyle(RED);
		} else {
			ctx.setFillStyle(GREY);
		}
		ctx.fillRect(x+width/2-1, y-0.5, width/2, height);
		ctx.strokeRect(x-0.5, y-0.5, width, height);
	}
	
	public static void drawCompRect(Context2d ctx, double x, double y, 
			double width, double height) {
		ctx.setStrokeStyle(BLACK);
		ctx.setLineWidth(2);
		ctx.setFillStyle(GREY);
		ctx.fillRect(x-0.5, y-0.5, width/2, height);
		ctx.fillRect(x+width/2-1, y-0.5, width/2, height);
		ctx.strokeRect(x-0.5, y-0.5, width, height);
	}
	
	public static void drawCompRect(Context2d ctx, double x, double y, 
			double width, double height, CssColor color) {
		ctx.setStrokeStyle(color);
		ctx.setLineWidth(2);
		ctx.fillRect(x-0.5, y-0.5, width/2, height);
		ctx.fillRect(x+width/2-1, y-0.5, width/2, height);
		ctx.strokeRect(x-0.5, y-0.5, width, height);
	}
	
	/**
	 * Draws an ellipse with the standard component colour scheme; black outline, light grey fill with optional red fill.
	 * 
	 * @param ctx		the context of the canvas on which the ellipse is to be drawn
	 * @param x			the x-coordinate of the bounding rectangle's top left corner
	 * @param y			the y-coordinate of the bounding rectangle's top left corner
	 * @param width		the width of the ellipse in pixels
	 * @param height	the height of the ellipse in pixels
	 * @param highlight	whether ellipse should be filled red to indicate the component it represents being in use
	 */
	public static void drawCompEllipse(Context2d ctx, double x, double y, 
			double width, double height, boolean highlight) {
		if (highlight) {
			drawEllipse(ctx, x, y, width, height, BLACK, RED);
		} else {
			drawEllipse(ctx, x, y, width, height, BLACK, GREY);
		}
	}
	
	/**
	 * Draws an ellipse.
	 * 
	 * @param ctx		the context of the canvas on which the ellipse is to be drawn
	 * @param x			the x-coordinate of the bounding rectangle's top left corner
	 * @param y			the y-coordinate of the bounding rectangle's top left corner
	 * @param width		the width of the ellipse in pixels
	 * @param height	the height of the ellipse in pixels
	 * @param stroke	the color of the ellipse outline
	 * @param fill		the ellipse fill color
	 */
	public static void drawEllipse(Context2d ctx, double x, double y, double width, double height, 
			CssColor stroke, CssColor fill) {
		double kappa = 0.5522848; 
		double ox = (width / 2) * kappa; // control point offset horizontal
		double oy = (height / 2) * kappa; // control point offset vertical
		double xe = x + width; // x-end
		double ye = y + height; // y-end
		double xm = x + width / 2; // x-middle
		double ym = y + height / 2; // y-middle
		ctx.setLineWidth(2);
		ctx.setFillStyle(fill);
		ctx.setStrokeStyle(stroke);
		ctx.beginPath();
		ctx.moveTo(x, ym);
		ctx.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);
		ctx.bezierCurveTo(xm + ox, y, xe, ym - oy, xe, ym);
		ctx.bezierCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
		ctx.bezierCurveTo(xm - ox, ye, x, ym + oy, x, ym);
		ctx.fill();
		ctx.stroke();
		ctx.closePath();
	}
	
	/**
	 * Draws a multiplexor with black outline and light grey fill. The highlight parameters 
	 * can be set to fill the top and or bottom halves of the mux red.
	 * 
	 * @param ctx				the context of the canvas on which the multiplexor is to be drawn
	 * @param x					the x-coordinate of the top left corner of the mux
	 * @param y					the y-coordinate of the top left corner of the mux
	 * @param width				the width of the mux in pixels
	 * @param height			the height of the mux in pixels
	 * @param highlightTop		whether the top half of the mux should be filled red to denote 
	 * 							it being the selected data path
	 * @param highlightBottom	whether the bottom half of the mux should be filled red to denote it
	 * 							being the selected data path 
	 */
	public static void drawMux(Context2d ctx, double x, double y, double width, double height, 
			boolean highlightTop, boolean highlightBottom) {
		ctx.setStrokeStyle(BLACK);
		ctx.setLineWidth(2);
		ctx.beginPath();
		ctx.moveTo(x, y+height/2);
		ctx.lineTo(x, y+(width/2));
		ctx.arc(x+(width/2), y+(width/2), width/2, Math.PI, 0, false);
		ctx.lineTo(x+width, y+height/2);
		if (highlightTop) {
			ctx.setFillStyle(RED);
		} else {
			ctx.setFillStyle(GREY);
		}
		ctx.fill();
		ctx.stroke();
		ctx.closePath();
		ctx.beginPath();
		ctx.moveTo(x+width, y+height/2);
		ctx.lineTo(x+width, y+height-(width/2));
		ctx.arc(x+(width/2), y+height-(width/2), width/2, 0, Math.PI, false);
		ctx.lineTo(x, y+height/2);
		if (highlightBottom) {
			ctx.setFillStyle(RED);
		} else {
			ctx.setFillStyle(GREY);
		}
		ctx.fill();
		ctx.stroke();
		ctx.closePath();
	}
	
	/**
	 * Draws an ALU with black outline and light grey fill. If the <code>highlight</code> parameter 
	 * is set to <code>true</code>, the fill wil be red.
	 * 
	 * @param ctx		the context of the canvas on which the ALU is to be drawn
	 * @param x			the x-coordinate of the top left corner of the ALU
	 * @param y			the y-coordinate of the top left corner of the ALU
	 * @param width		the width of the ALU in pixels
	 * @param height	the height of the ALU in pixels
	 * @param highlight	whether ALU should be filled red to denote it being in use
	 */
	public static void drawALU(Context2d ctx, double x, double y, double width, double height, boolean highlight) {
		if (highlight) {
			ctx.setFillStyle(RED);
		} else {
			ctx.setFillStyle(GREY);
		}
		ctx.setStrokeStyle(BLACK);
		ctx.setLineWidth(2);
		ctx.beginPath();
		ctx.moveTo(x,y);
		ctx.lineTo(x+width, y+(height/4));
		ctx.lineTo(x+width, y+(3*height/4));
		ctx.lineTo(x, y+height);
		ctx.lineTo(x, y+height-(3*height/8));
		ctx.lineTo(x+width/5, y+height/2);
		ctx.lineTo(x, y+(3*height/8));
		ctx.lineTo(x, y);
		ctx.fill();
		ctx.stroke();
		ctx.closePath();
	}
	
	/**
	 * Draws a vertical wire segment
	 * 
	 * @param ctx		the context of the canvas on which the segment is to be drawn
	 * @param x			the x-coordinate of the segment
	 * @param yBot		the y-coordinate of the start of the segment
	 * @param yTop		the y-coordinate of the end of the segment
	 * @param color		the color of the segment
	 * @param joinBot	whether a path-join circle should be drawn at the start of the segment
	 * @param joinTop	whether a path-join circle should be drawn at the end of the segment 
	 * @param lineWidth	the width of the line in pixels
	 */
	public static void drawVerticalSegment(Context2d ctx, double x, double yBot, double yTop, 
			CssColor color, boolean joinBot, boolean joinTop, double lineWidth) {
		ctx.setLineWidth(lineWidth);
		ctx.setFillStyle(color);
		if (joinBot) {
			ctx.beginPath();
			ctx.arc(x+0.5, yBot, 4, 0, Math.PI*2, true);
			ctx.closePath();
			ctx.fill();
		}
		if (joinTop) {
			ctx.beginPath();
			ctx.arc(x+0.5, yTop, 4, 0, Math.PI*2, true);
			ctx.closePath();
			ctx.fill();
		}
		ctx.fillRect(x-0.5-lineWidth/3, yTop-0.5, lineWidth, yBot-yTop+lineWidth);
	}
	
	/**
	 * Draws a horizontal wire segment.
	 * 
	 * @param ctx		the context of the canvas on which the segment is to be drawn
	 * @param xLeft		the x-coordinate of the start of the segment
	 * @param y			the y-coordinate of the segment
	 * @param xRight	the x-coordinate of the end of the segment
	 * @param color		the color of the segment
	 * @param joinLeft	whether a path-join circle should be drawn at the start of the segment
	 * @param joinRight	whether a path-join circle should be drawn at the end of the segment
	 * @param lineWidth	the width of the line in pixels
	 */
	public static void drawHorizontalSegment(Context2d ctx, double xLeft, double y, double xRight, 
			CssColor color, boolean joinLeft, boolean joinRight, double lineWidth) {
		ctx.setLineWidth(lineWidth);
		ctx.setFillStyle(color);
		if (joinLeft) {
			ctx.beginPath();
			ctx.arc(xLeft+0.5, y, 4, 0, Math.PI*2, true);
			ctx.closePath();
			ctx.fill();
		}
		if (joinRight) {
			ctx.beginPath();
			ctx.arc(xRight+0.5, y, 4, 0, Math.PI*2, true);
			ctx.closePath();
			ctx.fill();
		}
		ctx.fillRect(xLeft-0.5, y-0.5, xRight-xLeft, lineWidth);
	}
	
	/**
	 * Draws a vertical wire segment
	 * 
	 * @param ctx		the context of the canvas on which the segment is to be drawn
	 * @param x			the x-coordinate of the segment
	 * @param yBot		the y-coordinate of the start of the segment
	 * @param yTop		the y-coordinate of the end of the segment
	 * @param color		the color of the segment
	 * @param joinBot	whether a path-join circle should be drawn at the start of the segment
	 * @param joinTop	whether a path-join circle should be drawn at the end of the segment 
	 */
	public static void drawVerticalSegment(Context2d ctx, double x, double yBot, double yTop, 
			CssColor color, boolean joinBot, boolean joinTop) {
		drawVerticalSegment(ctx, x, yBot, yTop, color, joinBot, joinTop, 2);
	}
	
	/**
	 * Draws a horizontal wire segment.
	 * 
	 * @param ctx		the context of the canvas on which the segment is to be drawn
	 * @param xLeft		the x-coordinate of the start of the segment
	 * @param y			the y-coordinate of the segment
	 * @param xRight	the x-coordinate of the end of the segment
	 * @param color		the color of the segment
	 * @param joinLeft	whether a path-join circle should be drawn at the start of the segment
	 * @param joinRight	whether a path-join circle should be drawn at the end of the segment
	 */
	public static void drawHorizontalSegment(Context2d ctx, double xLeft, double y, double xRight, 
			CssColor color, boolean joinLeft, boolean joinRight) {
		drawHorizontalSegment(ctx, xLeft, y, xRight, color, joinLeft, joinRight, 2);
	}
	
	/**
	 * Draws a a vertical wire arrow pointing upward.
	 * 
	 * @param ctx		the context of the canvas on which the arrow is to be drawn
	 * @param x			the x-coordinate of the arrow
	 * @param yTail		the y-coordinate of the tail of the arrow
	 * @param yHead		the y-coordinate of the head of the arrow
	 * @param color		the color of the arrow
	 * @param join		whether a path-join circle should be drawn at the tail of the arrow
	 */
	public static void drawUpArrow(Context2d ctx, double x, double yTail, double yHead, 
			CssColor color, boolean join) {
		ctx.setLineWidth(2);
		ctx.setFillStyle(color);
		if (join) {
			ctx.beginPath();
			ctx.arc(x+0.5, yTail, 4, 0, Math.PI*2, true);
			ctx.closePath();
			ctx.fill();
		}
		ctx.fillRect(x-0.5, yHead+(8)-0.5, 2, yTail-yHead-8);
		drawUpArrowHead(ctx, x+0.5, yHead);
	}
	
	/**
	 * Draws a horizontal wire arrow pointing to the right.
	 * 
	 * @param ctx		the context of the canvas on which the arrow is to be drawn
	 * @param xTail		the context of the canvas on which the arrow is to be drawn
	 * @param y			the y-coordinate of the arrow
	 * @param xHead		the x-coordinate of the head of the arrow
	 * @param color		the colour of the arrow
	 * @param join		whether a path-join circle should be drawn at the tail of the arrow
	 */
	public static void drawRightArrow(Context2d ctx, double xTail, double y, double xHead,
			CssColor color, boolean join) {
		ctx.setLineWidth(2);
		ctx.setFillStyle(color);
		if (join) {
			ctx.beginPath();
			ctx.arc(xTail+0.5, y, 4, 0, Math.PI*2, true);
			ctx.closePath();
			ctx.fill();
		}
		ctx.fillRect(xTail-0.5, y-0.5, xHead-8-xTail, 2);
		drawRightArrowHead(ctx, xHead, y+0.5);
	}
	
	/**
	 * Draws a horizontal wire arrow pointing to the left.
	 * @param ctx 		the context of the canvas on which the arrow is to be drawn
	 * @param xTail		the context of the canvas on which the arrow is to be drawn
	 * @param y			the y-coordinate of the arrow
	 * @param xHead		the x-coordinate of the head of the arrow
	 * @param color		the colour of the arrow
	 * @param join		whether a path-join circle should be drawn at the tail of the arrow
	 */
	
	public static void drawLeftArrow(Context2d ctx, double xTail, double y, double xHead, 
			CssColor color, boolean join) {
		ctx.setLineWidth(2);
		ctx.setFillStyle(color);
		if (join) {
			ctx.beginPath();
			ctx.arc(xTail+0.5, y, 4, 0, Math.PI*2, true);
			ctx.closePath();
			ctx.fill();
		}
		ctx.fillRect(xTail-0.5, y-0.5, xHead-8-xTail, 2);
		drawLeftArrowHead(ctx, xHead, y+0.5);
	}
	
	private static void drawRightArrowHead(Context2d ctx, double x, double y) {
		ctx.beginPath();
		ctx.moveTo(x,y);
		ctx.lineTo(x-10, y-4);
		ctx.lineTo(x-10, y+4);
		ctx.lineTo(x,y);
		ctx.fill();
		ctx.closePath();
	}
	
	private static void drawUpArrowHead(Context2d ctx, double x, double y) {
		ctx.beginPath();
		ctx.moveTo(x,y);
		ctx.lineTo(x-4, y+10);
		ctx.lineTo(x+4, y+10);
		ctx.lineTo(x,y);
		ctx.fill();
		ctx.closePath();
	}
	
	private static void drawLeftArrowHead(Context2d ctx, double x, double y) {
		ctx.beginPath();
		ctx.moveTo(x, y);
		ctx.lineTo(x+10, y-4);
		ctx.lineTo(x+10, y+4);
		ctx.lineTo(x, y);
		ctx.fill();
		ctx.closePath();
	}
}

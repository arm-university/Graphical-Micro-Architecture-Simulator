
# Graphical-Micro-Architecture-Simulator

The Graphical Micro-Architecture Simulator is a web browser-based simulator for a subset of the Arm instructions. Specifically, this tool provides the expected execution results for the LEGv8 instructions, which is a subset of Arm®v8-A instructions based on the [Computer Organization and Design (Arm Edition)](https://www.elsevier.com/books/computer-organization-and-design-arm-edition/patterson/978-0-12-801733-3) textbook by David A. Patterson & John L. Hennessy. This simulator can be run locally on a PC, and is offered exclusively and at no cost to academics, teaching staff, and learners worldwide.

### You can try online the forked simulator [HERE](https://simdeistud.github.io/LEGv8-Simulator/).

## What has been added to this forked version

* **Integrated all the instructions of the LEGv8 ISA**, including the floating point operations.
* Fixed all the major bugs that impeded the correct execution of code. Corrected stack base to be quadword aligned.
* Integrated the project with Maven in order to make importing, configuration and development effortless.
* Added a visualization for the stack and for the floating point registers. Reorganized the UI to best fit the new additions.
* The project and the AceGWT library have been updated to use GWT 2.11.0

This effort has been made as a Computer Engineering bachelor's thesis in the context of the **_Digital Systems Architectures_** course held at the **_Department of Engineering and Architecture_** at the **_[University of Trieste](https://www.units.it/en)_** under the guidance of _**Prof. Alberto Carini**_ .

## How to develop and package the project

### IntelliJ IDEA
Choose the `Get from VCS` option and copy paste this repository's git link. The Maven project will be cloned into your workspace and the dependencies will be automatically downloaded and configured.
After making your changes you can build and package the simulator by going to the Maven panel on the right, navigate to `Graphical-Micro-Architecture-Simulator -> LEGv8_Simulator -> Lifecycle -> package` .
The folder containing the simulator will appear under `LEGv8_Simulator/target/` . 

IntelliJ Ultimate offers a GWT plugin that warns the programmer when using methods, syntax and classes not implemented by GWT and can generate compile reports.
Be warned that in some cases it might show bogous errors (such as missing css directives), 
but this shouldn't affect the compilation which is done with GWT.
If you use IntelliJ without this plugin the errors will disappear but you will not have access to said features.

### Eclipse
Choose `Import projects...`, select `Git/Projects from Git (with smart import)` and copy paste this repository's git link. Keep pressing `Next` until it has finished the procedure. It is recommended to go to `Window -> Preferences -> XML (Wild Web Developer)` and enable the download of external resources. Unlike IntelliJ, Eclipse doesn't show directly the `package` action but has to be added manually by right clicking on the `LEGv8_Simulator` folder and going to `Run As -> Run Configurations...` and then double click on `Maven Build`. This will create a new configuration for you to edit: give it the name `package`, select `Workspace... -> LEGv8_Simulator` as the `Base Directory` and write `package` inside the `Goals` text box. Now you can `Apply` and run it. To run it again just go to the `Run As` menu as before and it should have been added there.
The folder containing the simulator will appear under `LEGv8_Simulator/target/` (press F5 in Eclipse to refresh the folders).

Eclipse offers a GWT plugin (which has installation problems with Eclipse versions newer than the 2023-09) that makes compilation easier 
but unlike the Maven `package` action deploys the compiled sources into a `war` folder and doesn't automatically copy-paste the web 
resources needed to launch the web page. That has to be done manually. This plugin uses the older build method and is not recommended.

### Apache NetBeans

Clone the repository to a location of your choosing. In NetBeans go to `File -> Open Project...` and select the cloned repository folder. To access the LEGv8_Simulator files in the IDE go to `Graphical-Micro-Architecture-Simulator -> Modules` and double click on `LEGv8_Simulator`. This will open the module in the project browser. In order to build and package the simulator, right click on `LEGv8_Simulator -> Run Maven -> Goals...` and write `package` into the Goals text field and press OK.
The folder containing the simulator will appear under `LEGv8_Simulator/target/` . 

### Command Line
Clone the repository to a folder of your choosing, make your changes to the files and run the `mvn package` command inside the `LEGv8_Simulator` folder.
 The folder containing the simulator will appear under `LEGv8_Simulator/target/`.

**GWT only implements a subset of the Java 8 JRE, this means you need to limit your syntax to this version and not every
feature might be usable.**

**Note: This tool is currently a BETA version.**

 
 ## Kit features

* Cross-platform, no installation required.
* Contains a code editor, with a visible and interactive register file.
* Shows visual datapaths similar to those in the [Computer Organization and Design (Arm Edition)](https://www.elsevier.com/books/computer-organization-and-design-arm-edition/patterson/978-0-12-801733-3) textbook by David A. Patterson & John L. Hennessy. 
* Execution mode:
    * Has ability to step through execution of instructions.
    * Provides expected results (register contents, flags).
    * Contains single-cycle CPU and pipelined CPU view.
    * Highlights datapaths of the CPU that uses a specific instruction. 
* Documentation provided.
* **Prerequisites:** Basics of assembly programming.


## Purpose of the Simulator
To produce learners who can program using Arm instructions and describe the corresponding datapaths involved in a single-cycle or a pipelined CPU. The simulator also provides error messages/feedback for learning purposes. 

A fundamental understanding of an Instruction Set Architecture (ISA) is a crucial skill for all aspiring hardware designers and developers. The ISA acts as the interface between hardware and software, specifying what the instruction set can do and how the processor makes use of those instructions - helping developers write more efficient code.

Sometimes, however, students struggle to understand how the ISA executes complex commands, for instance for the CPU, databus, memory and I/O. This simulator simplifies this interaction by allowing students to visualize the instruction execution, establishing a clear link between the inner architecture of a microprocessor and the instructions required to efficiently execute commands on the hardware.

**This tool is currently a BETA version. If there are any issues, please read the Reporting Bugs section in [here.](https://github.com/arm-university/Graphical-Micro-Architecture-Simulator/blob/main/Contributions_and_Modifications/Contributions_And_Modifications.md)**

## How to Use
Either clone this repository or [download the simulator here.](https://github.com/arm-university/Graphical-Micro-Architecture-Simulator/archive/refs/heads/main.zip)


1.	Navigate to `LEGv8_Simulator/target` directory and open `LEGv8_Simulator-1.0-SNAPSHOT/LEGv8_Simulator.html` using a web browser. 
2.	Click the **Help** tab on the top right of the simulator, which contains further documentation on usage. 

## Modifications and Contributions
We welcome any bug fixes, modifications, and contributions to this tool. For more information, please click [here.](https://github.com/arm-university/Graphical-Micro-Architecture-Simulator/tree/main/Contributions_and_Modifications)

## License
You are free to fork or clone this material. See [LICENSE.md](https://github.com/arm-university/Graphical-Micro-Architecture-Simulator/blob/main/License/LICENSE.md) for the complete license.

## Inclusive Language Commitment
Arm is committed to making the language we use inclusive, meaningful, and respectful. Our goal is to remove and replace non-inclusive language from our vocabulary to reflect our values and represent our global ecosystem.
 
Arm is working actively with our partners, standards bodies, and the wider ecosystem to adopt a consistent approach to the use of inclusive language and to eradicate and replace offensive terms. We recognise that this will take time. This tool may contain references to non-inclusive language; it will be updated with newer terms as those terms are agreed and ratified with the wider community. We recognise that some of you will be accustomed to using the previous terms and may not immediately recognise their replacements. We encourage the use of the following terminology where applicable:

* When introducing the architecture, we will use the term ‘Requester’ instead of ‘Master’ and ‘Completer’ instead of ‘Slave’. 

 
Contact us at education@arm.com with questions or comments about this tool. You can also report non-inclusive and offensive terminology usage in Arm content at terms@arm.com.

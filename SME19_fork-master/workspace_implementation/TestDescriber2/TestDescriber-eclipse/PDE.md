# 1. Install Eclipse PDE
To obtain PDE, in the top menu, select "Help" > "Install New Software". 

<img width="743" alt="install-new-software" src="https://user-images.githubusercontent.com/2140361/33655662-ff67339c-da73-11e7-84bc-8dda4d229779.png">

Select Work with: "--All Available Sites--", in the search field enter "pde", check "Eclipse Plugin Development Tools".

<img width="641" alt="install-pde" src="https://user-images.githubusercontent.com/2140361/33655887-ac266fa8-da74-11e7-9daf-b72c85ab99a8.png">

Click "Next". Accept the license agreement and select "Finish". You will need to restart Eclipse.

# 2. Edit plugin
The eclipse plugin may be modified by importing the project and opening the "plugin.xml" file in the PDE.

This should open the PDE interface, whereby tabs along the bottom of the PDE interface expose plugin configuration.

# 2. Compile plugin
In the top right of the PDE you should notice an export button.

Clicking this provides the option of compiling the project as a jar bundle for Eclipse.

# 3. Install plugin
Place the jar bundle in "$eclipse/dropins" to install it, and restart Eclipse.

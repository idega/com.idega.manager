<?xml version="1.0"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:w="http://xmlns.idega.com/com.idega.webface" version="1.2">

	<f:view>
		<w:workspace_page id="workspace1" >
			<head>
                <meta content="no-cache" http-equiv="Cache-Control"/>
                <meta content="no-cache" http-equiv="Pragma"/>
                <title>UpdateListManager Title</title>
                <link href="resources/stylesheet.css" rel="stylesheet" type="text/css"/>
            </head>
            <body style="-rave-layout: grid">
                <h:form binding="#{UpdateListManager.form1}" id="form1" style="height: 592px; width: 857px">
                    <h:outputText binding="#{UpdateListManager.outputText1}" id="outputText1" style="height: 26px; left: 48px; top: 48px; position: absolute; width: 216px"
                    value="#{UpdateListManager.outputText1Value}"/>
                    <h:outputText binding="#{UpdateListManager.outputText2}" id="outputText2" style="height: 24px; left: 48px; top: 96px; position: absolute; width: 216px"
                    value="#{UpdateListManager.outputText2Value}"/>
                    <h:selectManyListbox binding="#{UpdateListManager.multiSelectListbox1}" id="multiSelectListbox1" 
                    style="height: 150px; width: 400px; left: 48px; top: 144px; position: absolute"
                    validator="#{UpdateListManager.validateSelectedModules}">
                        <f:selectItems binding="#{UpdateListManager.multiSelectListbox1SelectItems}" id="multiSelectListbox1SelectItems" value="#{UpdateListManager.multiSelectListbox1DefaultItems}"/>
                    </h:selectManyListbox>
                    <h:commandButton binding="#{UpdateListManager.button1}" id="button1"  
                       	value="#{UpdateListManager.button1Label}"
                        style="height: 24px; left: 48px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{UpdateListManager.button2}" id="button2" 
                    	action="#{UpdateListManager.button2_action}"
                    	actionListener="#{UpdateListManager.submitForm}"
                    	value="#{UpdateListManager.button2Label}"
                        style="height: 24px; left: 168px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{UpdateListManager.button3}" id="button3"
                    	value="#{UpdateListManager.button3Label}"
                        style="height: 24px; left: 288px; top: 312px; position: absolute; width: 96px"/>
                     <h:message for="form1:multiSelectListbox1" showSummary="true"
                     style="color: red; ffont-family: 'New Century Schoolbook', serif;   font-style: oblique;   text-decoration: overline" id="errors1"/>
                </h:form>
            </body>
        </w:workspace_page>
    </f:view>
</jsp:root>

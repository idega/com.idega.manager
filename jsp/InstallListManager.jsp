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
                <title>InstallListManager Title</title>
                <link href="resources/stylesheet.css" rel="stylesheet" type="text/css"/>
            </head>
            <body style="-rave-layout: grid">
                <h:form binding="#{InstallListManager.form1}" id="form1" style="height: 496px; width: 865px">
                    <h:outputText binding="#{InstallListManager.outputText1}" id="outputText1" style="height: 24px; left: 48px; top: 48px; position: absolute; width: 264px"/>
                    <h:outputText binding="#{InstallListManager.outputText2}" id="outputText2" style="height: 24px; left: 48px; top: 96px; position: absolute; width: 264px"/>
                    <h:selectManyListbox binding="#{InstallListManager.multiSelectListbox1}" id="multiSelectListbox1" style="height: 288px; left: 48px; top: 144px; position: absolute; width: 552px">
                        <f:selectItems binding="#{InstallListManager.multiSelectListbox1SelectItems}" id="multiSelectListbox1SelectItems" value="#{InstallListManager.multiSelectListbox1DefaultItems}"/>
                    </h:selectManyListbox>
                    <h:commandButton binding="#{InstallListManager.button1}" id="button1"
                        style="height: 24px; left: 48px; top: 456px; position: absolute; width: 96px" value="Submit"/>
                    <h:commandButton binding="#{InstallListManager.button2}" id="button2"
                        style="height: 24px; left: 168px; top: 456px; position: absolute; width: 96px" value="Submit"/>
                    <h:commandButton binding="#{InstallListManager.button3}" id="button3"
                        style="height: 24px; left: 288px; top: 456px; position: absolute; width: 96px" value="Submit"/>
                </h:form>
            </body>
        </w:workspace_page>
    </f:view>
</jsp:root>

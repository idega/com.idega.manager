<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">
    <jsp:directive.page contentType="text/html;charset=ISO-8859-1" pageEncoding="UTF-8"/>
    <f:view>
        <html lang="en-US" xml:lang="en-US">
			<head>
                <meta content="no-cache" http-equiv="Cache-Control"/>
                <meta content="no-cache" http-equiv="Pragma"/>
                <title>InstallListManager</title>
                <link href="../idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css" rel="stylesheet" type="text/css"/>
                <link href="../../idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css" rel="stylesheet" type="text/css"/>
            </head>
            <body class="wf_body" style="-rave-layout: grid">
            	<h:panelGroup binding="#{InstallListManager.groupPanel1}" id="groupPanel1" style="height: 144px; left: 48px; top: 4px; position: absolute; width: 312px">
                </h:panelGroup>
                <h:form binding="#{InstallListManager.form1}" id="form1" style="height: 592px; width: 857px">
                    <h:outputText binding="#{InstallListManager.outputText1}" id="outputText1" style="height: 26px; left: 48px; top: 48px; position: absolute; width: 216px"
                    value="#{InstallListManager.outputText1Value}"/>
                    <h:outputText binding="#{InstallListManager.outputText2}" id="outputText2" style="height: 24px; left: 48px; top: 96px; position: absolute; width: 216px"
                    value="#{InstallListManager.outputText2Value}"/>
                    <h:messages showSummary="false" showDetail="true"
            		style="color: red; ffont-family: 'New Century Schoolbook', serif;   font-style: oblique;   text-decoration: overline" id="errors1"/>
                    <h:selectManyListbox binding="#{InstallListManager.multiSelectListbox1}" id="multiSelectListbox1" 
                    style="height: 160px; width: 500px; left: 48px; top: 144px; position: absolute">
                        <f:selectItems binding="#{InstallListManager.multiSelectListbox1SelectItems}" id="multiSelectListbox1SelectItems" value="#{InstallListManager.multiSelectListbox1DefaultItems}"/>
                    </h:selectManyListbox>
                    <h:inputHidden value="noSelection" validator="#{InstallListManager.validateSelectedModules}"/>
                    <h:commandButton binding="#{InstallListManager.button2}" id="button2" 
                    	action="#{InstallListManager.button2_action}"
                    	actionListener="#{InstallListManager.submitForm}"
                    	value="#{InstallListManager.button2Label}"
                        style="height: 24px; left: 168px; top: 312px; position: absolute; width: 96px"/>
                    </h:form>
                    <h:form>                        
                    <h:commandButton binding="#{InstallListManager.button1}" id="button1"  
                        action="#{InstallListManager.button1_action}"
                       	value="#{InstallListManager.button1Label}"
                        style="height: 24px; left: 48px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{InstallListManager.button3}" id="button3"
                    	action="#{InstallListManager.button3_action}"
                    	value="#{InstallListManager.button3Label}"
                        style="height: 24px; left: 288px; top: 312px; position: absolute; width: 96px"/>
                </h:form>
            </body>
        </html>
    </f:view>
</jsp:root>

<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">
    <jsp:directive.page contentType="text/html;charset=ISO-8859-1" pageEncoding="UTF-8"/>
    <f:view>
        <html lang="en-US" xml:lang="en-US">
			<head>
                <meta content="no-cache" http-equiv="Cache-Control"/>
                <meta content="no-cache" http-equiv="Pragma"/>
                <title>InstallNewModuleListManager</title>
                <link href="../idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css" rel="stylesheet" type="text/css"/>
                <link href="../../idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css" rel="stylesheet" type="text/css"/>
            </head>
            <body class="wf_body" style="-rave-layout: grid">
                <h:form binding="#{InstallNewModuleListManager.form1}" id="form1" style="height: 592px; width: 857px">
                    <h:outputText binding="#{InstallNewModuleListManager.outputText1}" id="outputText1" style="height: 26px; left: 48px; top: 48px; position: absolute; width: 216px"
                    value="#{InstallNewModuleListManager.outputText1Value}"/>
                    <h:outputText binding="#{InstallNewModuleListManager.outputText2}" id="outputText2" style="height: 24px; left: 48px; top: 96px; position: absolute; width: 216px"
                    value="#{InstallNewModuleListManager.outputText2Value}"/>
                    <h:messages showSummary="false" showDetail="true"
            		style="color: red; ffont-family: 'New Century Schoolbook', serif;   font-style: oblique;   text-decoration: overline" id="errors1"/>
                    <h:selectManyListbox binding="#{InstallNewModuleListManager.multiSelectListbox1}" id="multiSelectListbox1" 
                    style="height: 160px; width: 500px; left: 48px; top: 144px; position: absolute">
                        <f:selectItems binding="#{InstallNewModuleListManager.multiSelectListbox1SelectItems}" id="multiSelectListbox1SelectItems" value="#{InstallNewModuleListManager.multiSelectListbox1DefaultItems}"/>
                    </h:selectManyListbox>
                    <h:inputHidden value="noSelection" validator="#{InstallNewModuleListManager.validateSelectedModules}"/>
                    <h:commandButton binding="#{InstallNewModuleListManager.button2}" id="button2" 
                    	action="#{InstallNewModuleListManager.button2_action}"
                    	actionListener="#{InstallNewModuleListManager.submitForm}"
                    	value="#{InstallNewModuleListManager.button2Label}"
                        style="height: 24px; left: 168px; top: 312px; position: absolute; width: 96px"/>
					</h:form>
                    <h:form>
                    <h:commandButton binding="#{InstallNewModuleListManager.button1}" id="button1"  
                        action="#{InstallNewModuleListManager.button1_action}"
                       	value="#{InstallNewModuleListManager.button1Label}"
                        style="height: 24px; left: 48px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{InstallNewModuleListManager.button3}" id="button3"
                    	action="#{InstallNewModuleListManager.button3_action}"
                    	value="#{InstallNewModuleListManager.button3Label}"
                        style="height: 24px; left: 288px; top: 312px; position: absolute; width: 96px"/>
                </h:form>
            </body>
        </html>
    </f:view>
</jsp:root>

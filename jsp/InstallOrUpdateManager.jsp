<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">
    <jsp:directive.page contentType="text/html;charset=ISO-8859-1" pageEncoding="UTF-8"/>
    <f:view>
        <html lang="en-US" xml:lang="en-US">
			<head>
                <meta content="no-cache" http-equiv="Cache-Control"/>
                <meta content="no-cache" http-equiv="Pragma"/>
                <title>InstallOrUpdateManager</title>
                <link href="../idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css" rel="stylesheet" type="text/css"/>
                <link href="../../idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css" rel="stylesheet" type="text/css"/>
           </head>
            <body class="wf_body" style="-rave-layout: grid">
                <h:form binding="#{InstallOrUpdateManager.form1}" id="form1">
                    <h:outputText binding="#{InstallOrUpdateManager.outputText1}" id="outputText1" style="height: 26px; left: 48px; top: 48px; position: absolute; width: 216px"
                    value="#{InstallOrUpdateManager.outputText1Value}"/>
                    <h:outputText binding="#{InstallOrUpdateManager.outputText2}" id="outputText2" style="height: 24px; left: 48px; top: 96px; position: absolute; width: 216px"
                    value="#{InstallOrUpdateManager.outputText2Value}"/>
                    <h:panelGroup binding="#{InstallOrUpdateManager.groupPanel1}" id="groupPanel1" style="height: 144px; left: 48px; top: 144px; position: absolute; width: 312px">
                        <h:selectOneRadio binding="#{InstallOrUpdateManager.radioButtonList1}" id="radioButtonList1" layout="pageDirection">
                            <f:selectItems binding="#{InstallOrUpdateManager.radioButtonList1SelectItems}" id="radioButtonList1SelectItems" value="#{InstallOrUpdateManager.radioButtonList1DefaultItems}"/>
                        </h:selectOneRadio>
                    </h:panelGroup>
                    <h:commandButton binding="#{InstallOrUpdateManager.button1}" id="button1"  disabled="true"
                    	value="#{InstallOrUpdateManager.button1Label}"
                        style="height: 24px; left: 48px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{InstallOrUpdateManager.button2}" id="button2" action="#{InstallOrUpdateManager.button2_action}"
                    	value="#{InstallOrUpdateManager.button2Label}"
                        style="height: 24px; left: 168px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{InstallOrUpdateManager.button3}" id="button3" disabled="true"
                    	value="#{InstallOrUpdateManager.button3Label}"
                        style="height: 24px; left: 288px; top: 312px; position: absolute; width: 96px"/>
                </h:form>
            </body>
        </html>
    </f:view>
</jsp:root>
<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">
    <jsp:directive.page contentType="text/html;charset=ISO-8859-1" pageEncoding="UTF-8"/>
    <f:view>
        <html lang="en-US" xml:lang="en-US">
            <head>
                <meta content="no-cache" http-equiv="Cache-Control"/>
                <meta content="no-cache" http-equiv="Pragma"/>
                <title>InstallOrUpdateManager Title</title>
                <link href="resources/stylesheet.css" rel="stylesheet" type="text/css"/>
            </head>
            <body style="-rave-layout: grid">
                <h:form binding="#{InstallOrUpdateManager.form1}" id="form1" style="height: 592px; width: 857px">
                    <h:outputText binding="#{InstallOrUpdateManager.outputText1}" id="outputText1" style="height: 24px; left: 48px; top: 48px; position: absolute; width: 240px"/>
                    <h:outputText binding="#{InstallOrUpdateManager.outputText2}" id="outputText2" style="height: 24px; left: 48px; top: 96px; position: absolute; width: 240px"/>
                    <h:selectOneRadio binding="#{InstallOrUpdateManager.radioButtonList1}" id="radioButtonList1" layout="pageDirection" style="height: 139px; left: 48px; top: 144px; position: absolute; width: 120px">
                        <f:selectItems binding="#{InstallOrUpdateManager.radioButtonList1SelectItems}" id="radioButtonList1SelectItems" value="#{InstallOrUpdateManager.radioButtonList1DefaultItems}"/>
                    </h:selectOneRadio>
                    <h:commandButton binding="#{InstallOrUpdateManager.button1}" id="button1"
                        style="height: 24px; left: 48px; top: 264px; position: absolute; width: 96px" value="Submit"/>
                    <h:commandButton binding="#{InstallOrUpdateManager.button2}" id="button2"
                        style="height: 24px; left: 168px; top: 264px; position: absolute; width: 96px" value="Submit"/>
                    <h:commandButton binding="#{InstallOrUpdateManager.button3}" id="button3"
                        style="height: 24px; left: 288px; top: 264px; position: absolute; width: 96px" value="Submit"/>
                </h:form>
            </body>
        </html>
    </f:view>
</jsp:root>

<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">
    <jsp:directive.page contentType="text/html;charset=ISO-8859-1" pageEncoding="UTF-8"/>
    <f:view>
        <html lang="en-US" xml:lang="en-US">
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
        </html>
    </f:view>
</jsp:root>

<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">
    <jsp:directive.page contentType="text/html;charset=ISO-8859-1" pageEncoding="UTF-8"/>
    <f:view>
        <html lang="en-US" xml:lang="en-US">
            <head>
                <meta content="no-cache" http-equiv="Cache-Control"/>
                <meta content="no-cache" http-equiv="Pragma"/>
                <title>InstallManager Title</title>
                <link href="resources/stylesheet.css" rel="stylesheet" type="text/css"/>
            </head>
            <body style="-rave-layout: grid">
                <h:form binding="#{InstallManager.form1}" id="form1">
                    <h:outputText binding="#{InstallManager.outputText1}" id="outputText1" style="height: 26px; left: 48px; top: 48px; position: absolute; width: 216px"
                    value="#{InstallManager.outputText1Value}"/>
                    <h:outputText binding="#{InstallManager.outputText2}" id="outputText2" style="height: 24px; left: 48px; top: 96px; position: absolute; width: 216px"
                    value="#{InstallManager.outputText2Value}"/>
                       <h:dataTable binding="#{InstallManager.dataTable1}" headerClass="list-header" id="dataTable1" rowClasses="list-row-even,list-row-odd"
                        style="height: 264px; left: 72px; top: 72px; position: absolute" value="#{InstallManager.dataTable1Model}" var="currentRow" width="432">
                    </h:dataTable>
                    <h:commandButton binding="#{InstallManager.button1}" id="button1"  
                       	value="#{InstallManager.button1Label}"
                        style="height: 24px; left: 48px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{InstallManager.button2}" id="button2" action="#{InstallManager.button2_action}"
                    	value="#{InstallManager.button2Label}"
                        style="height: 24px; left: 168px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{InstallManager.button3}" id="button3"
                    	value="#{InstallManager.button3Label}"
                        style="height: 24px; left: 288px; top: 312px; position: absolute; width: 96px"/>
                </h:form>
            </body>
        </html>
    </f:view>
</jsp:root>

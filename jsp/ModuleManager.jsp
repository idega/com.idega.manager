<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">
    <jsp:directive.page contentType="text/html;charset=ISO-8859-1" pageEncoding="UTF-8"/>
    <f:view>
        <html lang="en-US" xml:lang="en-US">
			<head>
                <meta content="no-cache" http-equiv="Cache-Control"/>
                <meta content="no-cache" http-equiv="Pragma"/>
                <title>ModuleManager Title</title>
                <link href="resources/stylesheet.css" rel="stylesheet" type="text/css"/>
            </head>
            <body style="-rave-layout: grid">
                <h:form binding="#{ModuleManager.form1}" id="form1">
                    <h:outputText binding="#{ModuleManager.outputText1}" id="outputText1" style="height: 26px; left: 48px; top: 48px; position: absolute; width: 216px"
                    value="#{ModuleManager.outputText1Value}"/>
                    <h:outputText binding="#{ModuleManager.outputText2}" id="outputText2" style="height: 24px; left: 48px; top: 96px; position: absolute; width: 216px"
                    value="#{ModuleManager.outputText2Value}"/>
                       <h:dataTable binding="#{ModuleManager.dataTable1}" headerClass="list-header" id="dataTable1" rowClasses="list-row-even,list-row-odd" border="2"
                        style="height: 150px; left: 48px; top: 144px; position: absolute" value="#{ModuleManager.dataTable1Model}" var="currentRow">
                    </h:dataTable>
                    <h:commandButton binding="#{ModuleManager.button1}" id="button1"  
                       	value="#{ModuleManager.button1Label}"
                        style="height: 24px; left: 48px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{ModuleManager.button2}" id="button2" 
                    	actionListener="#{ModuleManager.submitForm}"
                    	action="#{ModuleManager.button2_action}"
                    	value="#{ModuleManager.button2Label}"
                        style="height: 24px; left: 168px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{ModuleManager.button3}" id="button3"
                    	value="#{ModuleManager.button3Label}"
                        style="height: 24px; left: 288px; top: 312px; position: absolute; width: 96px"/>
                    <h:messages showSummary="true"
                    style="color: red; ffont-family: 'New Century Schoolbook', serif;   font-style: oblique;   text-decoration: overline" id="errors1"/>
                </h:form>
            </body>
        </html>
    </f:view>
</jsp:root>

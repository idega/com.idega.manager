<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">
    <jsp:directive.page contentType="text/html;charset=ISO-8859-1" pageEncoding="UTF-8"/>
    <f:view>
        <html lang="en-US" xml:lang="en-US">
			<head>
                <meta content="no-cache" http-equiv="Cache-Control"/>
                <meta content="no-cache" http-equiv="Pragma"/>
                <title>LoginManager</title>
                <link href="../idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css" rel="stylesheet" type="text/css"/>
                <link href="../../idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css" rel="stylesheet" type="text/css"/>
           </head>
            <body class="wf_body" style="-rave-layout: grid">
                <h:form binding="#{LoginManager.form1}" id="form1">
                    <h:outputText binding="#{LoginManager.outputText1}" id="outputText1" style="height: 26px; left: 48px; top: 48px; position: absolute; width: 216px"
                    value="#{LoginManager.outputText1Value}"/>
                    <h:outputText binding="#{LoginManager.outputText2}" id="outputText2" style="height: 24px; left: 48px; top: 96px; position: absolute; width: 216px"
                    value="#{LoginManager.outputText2Value}"/>
 
                    <h:messages showSummary="false" showDetail="true"
            		style="color: red; ffont-family: 'New Century Schoolbook', serif;   font-style: oblique;   text-decoration: overline" id="errors1"/>
                   
                   	<h:inputText binding="#{LoginManager.textField1}" id="textField1" style="height: 24px; left: 144px; top: 144px; position: absolute; width: 240px"/>
                    <h:inputText binding="#{LoginManager.textField2}" id="textField2" style="height: 24px; left: 144px; top: 192px; position: absolute; width: 240px"/>
                    <h:inputSecret binding="#{LoginManager.secretField1}" id="secretField1" style="height: 24px; left: 144px; top: 240px; position: absolute; width: 240px"/>

                    <h:outputText binding="#{LoginManager.outputText3}" id="outputText3" style="height: 24px; left: 48px; top: 144px; position: absolute; width: 72px"
                    value="#{LoginManager.outputText3Value}"/>
                    <h:outputText binding="#{LoginManager.outputText4}" id="outputText4" style="height: 24px; left: 48px; top: 192px; position: absolute; width: 72px"
                    value="#{LoginManager.outputText4Value}"/>
                    <h:outputText binding="#{LoginManager.outputText5}" id="outputText5" style="height: 24px; left: 48px; top: 240px; position: absolute; width: 72px"
                    value="#{LoginManager.outputText5Value}"/>
 
                    <h:inputHidden value="noSelection" validator="#{LoginManager.validateUserPassword}"/>
                    <h:commandButton binding="#{LoginManager.button2}" id="button2" 
                    	action="#{LoginManager.button2_action}"
                    	actionListener="#{LoginManager.submitForm}"
                    	value="#{LoginManager.button2Label}"
                        style="height: 24px; left: 168px; top: 312px; position: absolute; width: 96px"/>
                </h:form>
				<h:form>	
                   	<h:commandButton binding="#{LoginManager.button1}" id="button1"  disabled="true"
                    	value="#{LoginManager.button1Label}"
                        style="height: 24px; left: 48px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{LoginManager.button3}" id="button3" 
                    	action="#{LoginManager.button3_action}"
                    	value="#{LoginManager.button3Label}"
                        style="height: 24px; left: 288px; top: 312px; position: absolute; width: 96px"/>
                </h:form>
            </body>
        </html>
    </f:view>
</jsp:root>
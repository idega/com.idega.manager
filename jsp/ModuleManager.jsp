<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">
    <jsp:directive.page contentType="text/html;charset=ISO-8859-1" pageEncoding="UTF-8"/>
    <f:view>
        <html lang="en-US" xml:lang="en-US">
			<head>
                <meta content="no-cache" http-equiv="Cache-Control"/>
                <meta content="no-cache" http-equiv="Pragma"/>
                <title>ModuleManager</title>
                <link href="../idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css" rel="stylesheet" type="text/css"/>
                <link href="../../idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css" rel="stylesheet" type="text/css"/>
            </head>
            <body class="wf_body" style="-rave-layout: grid">
            	<h:panelGroup binding="#{ModuleManager.groupPanel1}" id="groupPanel1" style="height: 144px; left: 48px; top: 4px; position: absolute; width: 312px">
                </h:panelGroup>
              	<h:form binding="#{ModuleManager.form1}" id="form1">
                    <h:outputText binding="#{ModuleManager.outputText1}" id="outputText1" style="height: 26px; left: 48px; top: 48px; position: absolute; width: 216px"
                    value="#{ModuleManager.outputText1Value}"/>
                    <h:outputText binding="#{ModuleManager.outputText2}" id="outputText2" style="height: 24px; left: 48px; top: 96px; position: absolute; width: 216px"
                    value="#{ModuleManager.outputText2Value}"/>
                    <p class="datatable_area" style="left: 48px; top: 130px; position: absolute;">
                       	<h:dataTable style=" width: 500px"
                       	binding="#{ModuleManager.dataTable1}" headerClass="moduleManagerHeaderClass" id="dataTable1" rowClasses="list-row-even,list-row-odd" 
                       	cellpadding="1" cellspacing="1" 
                         value="#{ModuleManager.dataTable1Model}" var="currentRow">
                    	</h:dataTable>
                    </p>
                    <h:commandButton binding="#{ModuleManager.button1}" id="button1"  
                    	action="#{ModuleManager.button1_action}"
                       	value="#{ModuleManager.button1Label}"
                        style="height: 24px; left: 48px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{ModuleManager.button2}" id="button2" 
                    	actionListener="#{ModuleManager.submitForm}"
                    	action="#{ModuleManager.button2_action}"
                    	value="#{ModuleManager.button2Label}"
                        style="height: 24px; left: 168px; top: 312px; position: absolute; width: 96px"/>
                    <h:commandButton binding="#{ModuleManager.button3}" id="button3"
                    	action="#{ModuleManager.button3_action}"
                    	value="#{ModuleManager.button3Label}"
                        style="height: 24px; left: 288px; top: 312px; position: absolute; width: 96px"/>
                </h:form>
            </body>
        </html>
    </f:view>
</jsp:root>

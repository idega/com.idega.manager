<jsp:root version="1.2" 
	xmlns:f="http://java.sun.com/jsf/core" 
	xmlns:h="http://java.sun.com/jsf/html" 
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:ws="http://xmlns.idega.com/com.idega.workspace"
	xmlns:wf="http://xmlns.idega.com/com.idega.webface">
    <jsp:directive.page contentType="text/html;charset=ISO-8859-1" pageEncoding="UTF-8"/>
    <f:view>
		<ws:page>
        	<h:form id="form1">
            <wf:wfblock>
                
                <wf:container>
                    <h:outputText binding="#{InstallOrUpdateManager.outputText1}" id="outputText1" 
                    value="#{InstallOrUpdateManager.outputText1Value}"/>
                </wf:container>
                
                <wf:container>
                <h:outputText binding="#{InstallOrUpdateManager.outputText2}" id="outputText2"
                    value="#{InstallOrUpdateManager.outputText2Value}"/>
                </wf:container> 
                
                <wf:container>
                  	<h:panelGroup binding="#{InstallOrUpdateManager.groupPanel1}" id="groupPanel1">
                        <h:selectOneRadio binding="#{InstallOrUpdateManager.radioButtonList1}" 
                        id="radioButtonList1" 
                        layout="pageDirection">
                            <f:selectItems binding="#{InstallOrUpdateManager.radioButtonList1SelectItems}" 
                            id="radioButtonList1SelectItems" 
                            value="#{InstallOrUpdateManager.radioButtonList1DefaultItems}"/>
                        </h:selectOneRadio>
                    </h:panelGroup>
                </wf:container>
                
                <h:commandButton binding="#{InstallOrUpdateManager.button1}" id="button1"  
                  	disabled="true"
                   	value="#{InstallOrUpdateManager.button1Label}"/>
                
                <h:commandButton binding="#{InstallOrUpdateManager.button2}" id="button2" 
                   	action="#{InstallOrUpdateManager.button2_action}"
                   	value="#{InstallOrUpdateManager.button2Label}"/>
                
                <h:commandButton binding="#{InstallOrUpdateManager.button3}" id="button3" 
                   	immediate="true"
                   	action="#{InstallOrUpdateManager.button3_action}"
                   	value="#{InstallOrUpdateManager.button3Label}"/>
                
            </wf:wfblock>
            </h:form>
        </ws:page>
    </f:view>
</jsp:root>
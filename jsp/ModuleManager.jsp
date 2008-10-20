<jsp:root version="1.2" 
	xmlns:f="http://java.sun.com/jsf/core" 
	xmlns:h="http://java.sun.com/jsf/html" 
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:ws="http://xmlns.idega.com/com.idega.workspace"
	xmlns:wf="http://xmlns.idega.com/com.idega.webface">
    <jsp:directive.page contentType="text/html;charset=ISO-8859-1" pageEncoding="UTF-8"/>
    <f:view>
		<ws:page stylesheeturls="/idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css">
           	<h:form id="form1">
           	<wf:wfblock>
           	
           		<wf:container>
                    <h:outputText binding="#{ModuleManager.outputText1}" id="outputText1" 
                    value="#{ModuleManager.outputText1Value}"/>
                </wf:container>
                
                <wf:container>
                   <h:outputText binding="#{ModuleManager.outputText2}" id="outputText2" 
                    value="#{ModuleManager.outputText2Value}"/>
                </wf:container>
                
                <wf:container>       
                <!-- this panel group is used for user error messages -->
                   	<h:panelGroup binding="#{ModuleManager.groupPanel1}" id="groupPanel1">
                	</h:panelGroup>
                </wf:container>
                    
                <wf:container>
                   	<h:dataTable style=" width: 500px"
                   		binding="#{ModuleManager.dataTable1}" 
                   		headerClass="moduleManagerHeaderClass" 
                   		id="dataTable1" 
                   		rowClasses="list-row-even,list-row-odd" 
                   		cellpadding="1" 
                   		cellspacing="1" 
                    	value="#{ModuleManager.dataTable1Model}" 
                       	var="currentRow">
                   	</h:dataTable>
                </wf:container>
                
                <h:commandButton binding="#{ModuleManager.button1}" id="button1"  
                   	action="#{ModuleManager.button1_action}"
                   	value="#{ModuleManager.button1Label}"/>
                       	
                <h:commandButton binding="#{ModuleManager.button2}" id="button2" 
                   	actionListener="#{ModuleManager.submitForm}"
                   	action="#{ModuleManager.button2_action}"
                   	value="#{ModuleManager.button2Label}"/>
                    	
                <h:commandButton binding="#{ModuleManager.button3}" id="button3"
                   	action="#{ModuleManager.button3_action}"
                   	value="#{ModuleManager.button3Label}"/>
                   	
            </wf:wfblock>    
            </h:form>
        </ws:page>
    </f:view>
</jsp:root>

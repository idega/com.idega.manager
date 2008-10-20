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
                    <h:outputText binding="#{InstallNewModuleListManager.outputText1}" id="outputText1" 
                    	value="#{InstallNewModuleListManager.outputText1Value}"/>
                </wf:container>
                
                <wf:container>
                    <h:outputText binding="#{InstallNewModuleListManager.outputText2}" id="outputText2" 
                    	value="#{InstallNewModuleListManager.outputText2Value}"/>
                   	<h:messages 
                   		showSummary="false" 
                   		showDetail="true"
            			style="color: red; ffont-family: 'New Century Schoolbook', serif;   font-style: oblique;   text-decoration: overline" 
            			id="errors1"/>
            	</wf:container>
            	
            	<wf:container>
                    <h:selectManyListbox 
                    	binding="#{InstallNewModuleListManager.multiSelectListbox1}" 
                    	id="multiSelectListbox1"
                    	style="height: 160px; width: 500px">
                        <f:selectItems binding="#{InstallNewModuleListManager.multiSelectListbox1SelectItems}" 
                        	id="multiSelectListbox1SelectItems" 
                        	value="#{InstallNewModuleListManager.multiSelectListbox1DefaultItems}"/>
                    </h:selectManyListbox>
                </wf:container>
                
                <h:inputHidden value="noSelection" validator="#{InstallNewModuleListManager.validateSelectedModules}"/>
                    
                <h:commandButton binding="#{InstallNewModuleListManager.button1}" id="button1"  
                	immediate="true"
	                action="#{InstallNewModuleListManager.button1_action}"
                  	value="#{InstallNewModuleListManager.button1Label}"/>
                        
                <h:commandButton binding="#{InstallNewModuleListManager.button2}" id="button2" 
                    action="#{InstallNewModuleListManager.button2_action}"
                    actionListener="#{InstallNewModuleListManager.submitForm}"
                    value="#{InstallNewModuleListManager.button2Label}"/>
                        
                 <h:commandButton binding="#{InstallNewModuleListManager.button3}" id="button3"
                 	immediate="true"
                   	action="#{InstallNewModuleListManager.button3_action}"
                   	value="#{InstallNewModuleListManager.button3Label}"/>
                   	
            </wf:wfblock>
            </h:form>
        </ws:page>
    </f:view>
</jsp:root>

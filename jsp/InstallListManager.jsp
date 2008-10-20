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
                    <h:outputText binding="#{InstallListManager.outputText1}" id="outputText1" 
                    	value="#{InstallListManager.outputText1Value}"/>
                </wf:container>
                
                <wf:container>
                    <h:outputText binding="#{InstallListManager.outputText2}" id="outputText2" 
                    	value="#{InstallListManager.outputText2Value}"/>
                    <h:messages 
                    	showSummary="false" 
                    	showDetail="true"
            			style="color: red; ffont-family: 'New Century Schoolbook', serif;   font-style: oblique;   text-decoration: overline" 
            			id="errors1"/>
            	</wf:container>
            	
            	<wf:container>
            		<!-- this panel group is used for user error messages -->
                    <h:panelGroup binding="#{InstallListManager.groupPanel1}" id="groupPanel1">
                	</h:panelGroup>
                </wf:container>
                
                <wf:container>     
                    <h:selectManyListbox 
                    	binding="#{InstallListManager.multiSelectListbox1}" 
                    	id="multiSelectListbox1"
                    	style="height: 160px; width: 500px">
                        <f:selectItems binding="#{InstallListManager.multiSelectListbox1SelectItems}" 
                        	id="multiSelectListbox1SelectItems" 
                        	value="#{InstallListManager.multiSelectListbox1DefaultItems}"/>
                    </h:selectManyListbox>
                </wf:container>
                    
                <h:inputHidden value="noSelection" validator="#{InstallListManager.validateSelectedModules}"/>
                    
                <h:commandButton binding="#{InstallListManager.button1}" id="button1"  
                   	immediate="true"
                    action="#{InstallListManager.button1_action}"
                   	value="#{InstallListManager.button1Label}"/>
                                        
                <h:commandButton binding="#{InstallListManager.button2}" id="button2" 
                	action="#{InstallListManager.button2_action}"
                    actionListener="#{InstallListManager.submitForm}"
                    value="#{InstallListManager.button2Label}"/>
                    	
                <h:commandButton binding="#{InstallListManager.button3}" id="button3"
                   	immediate="true"
                   	action="#{InstallListManager.button3_action}"
                   	value="#{InstallListManager.button3Label}"/>
            
            </wf:wfblock>    
            </h:form>
        </ws:page>
    </f:view>
</jsp:root>

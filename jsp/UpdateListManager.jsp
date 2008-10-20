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
	            	<h:outputText binding="#{UpdateListManager.outputText1}" id="outputText1" 
	                    value="#{UpdateListManager.outputText1Value}"/>
	            </wf:container>
	            
	            <wf:container>
	                <h:outputText binding="#{UpdateListManager.outputText2}" id="outputText2" 
	                    value="#{UpdateListManager.outputText2Value}"/>
	                <h:messages 
	                	showSummary="false" 
	                	showDetail="true"
	            		style="color: red; ffont-family: 'New Century Schoolbook', serif;   font-style: oblique;   text-decoration: overline" 
	            		id="errors1"/>
	            </wf:container>
	            
	            <wf:container>
	            <!-- this panel group is used for user error messages -->
	                <h:panelGroup binding="#{UpdateListManager.groupPanel1}" id="groupPanel1">
	                </h:panelGroup>
	            </wf:container>
	            
	            
	            <wf:container>
	            	<h:selectManyListbox
	            		binding="#{UpdateListManager.multiSelectListbox1}"
	            		id="multiSelectListbox1"
	            		style="height: 160px; width: 500px">
	                	<f:selectItems binding="#{UpdateListManager.multiSelectListbox1SelectItems}" 
	                        id="multiSelectListbox1SelectItems" 
	                        value="#{UpdateListManager.multiSelectListbox1DefaultItems}"/>
	            	</h:selectManyListbox>
	            </wf:container>
	            
	            <h:inputHidden value="noSelection" validator="#{UpdateListManager.validateSelectedModules}"/>
	                    	
	            <h:commandButton binding="#{UpdateListManager.button1}" id="button1"  
	               	immediate="true"
	                action="#{UpdateListManager.button1_action}"
	               	value="#{UpdateListManager.button1Label}"/>
	               	
	            <h:commandButton binding="#{UpdateListManager.button2}" id="button2" 
	              	action="#{UpdateListManager.button2_action}"
	               	actionListener="#{UpdateListManager.submitForm}"
	               	value="#{UpdateListManager.button2Label}"/>	               	
	                       	
	            <h:commandButton binding="#{UpdateListManager.button3}" id="button3"
	              	immediate="true"
	               	action="#{UpdateListManager.button3_action}"
	               	value="#{UpdateListManager.button3Label}"/>
	                    	
           	</wf:wfblock>
            </h:form>
		</ws:page>
    </f:view>
</jsp:root>

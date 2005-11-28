<jsp:root version="1.2" 
	xmlns:f="http://java.sun.com/jsf/core" 
	xmlns:h="http://java.sun.com/jsf/html" 
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:ws="http://xmlns.idega.com/com.idega.workspace"
	xmlns:wf="http://xmlns.idega.com/com.idega.webface">
    <jsp:directive.page contentType="text/html"/><!--;charset=UTF-8" pageEncoding="UTF-8"-->
    <f:view>
		<ws:page id="systemsettingspage">
		<h:form id="systemsettingsform" > <!-- acceptCharset="UTF-8" -->
			<wf:wfblock id="serverpropertiesblock" title="#{localizedStrings['com.idega.manager']['systemsettings']}">
			<wf:container styleClass="wf_formitem" >
				<h:outputLabel for="mainDomainName" id="mainDomainNameLabel" value="#{localizedStrings['com.idega.manager']['mainDomainName']}"/>
				<h:inputText value="#{SystemSettings.mainDomainName}" id="mainDomainName"/>
			</wf:container>
		
			<wf:container styleClass="wf_formitem" >
				<h:outputLabel for="mainDomainUrl" id="mainDomainUrlLabel" value="#{localizedStrings['com.idega.manager']['mainDomainUrl']}"/>
				<h:inputText value="#{SystemSettings.mainDomainUrl}" id="mainDomainUrl"/>
			</wf:container>
		
			<wf:container styleClass="wf_formitem" >
				<h:outputLabel for="mainDomainServerName" id="mainDomainServerNameLabel" value="#{localizedStrings['com.idega.manager']['mainDomainServerName']}"/>
				<h:inputText value="#{SystemSettings.mainDomainServerName}" id="mainDomainServerName"/>
			</wf:container>
			
			<h:commandButton id="serverproperties_store" action="#{SystemSettings.store}" value="#{localizedStrings['com.idega.manager']['save']}"/>
			</wf:wfblock>
		</h:form>
		</ws:page>
    </f:view>
</jsp:root>

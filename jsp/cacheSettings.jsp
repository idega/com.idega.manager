<jsp:root version="1.2" 
	xmlns:f="http://java.sun.com/jsf/core" 
	xmlns:h="http://java.sun.com/jsf/html" 
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:ws="http://xmlns.idega.com/com.idega.workspace"
	xmlns:wf="http://xmlns.idega.com/com.idega.webface">
    <jsp:directive.page contentType="text/html"/><!--;charset=UTF-8" pageEncoding="UTF-8"-->
    <f:view>
		<ws:page id="cachesettingspage">
		<h:form id="cachesettingsform" > <!-- acceptCharset="UTF-8" -->
			<wf:wfblock id="cachesettingsblock" title="#{localizedStrings['com.idega.manager']['cachesettings']}">
				<wf:container styleClass="formitem" >
					<h:outputLabel for="enableGlobalPageCache" id="enableGlobalPageCacheLabel" value="#{localizedStrings['com.idega.manager']['enable_global_page_cache']}"/>
					<h:selectBooleanCheckbox value="#{CacheSettings.cacheFilterEnabled}" id="enableGlobalPageCache"/>
				</wf:container>
				<f:facet name="footer">
	              	<wf:container styleClass="buttons">
						<h:commandButton id="cachesettings_store" action="#{CacheSettings.store}" value="#{localizedStrings['com.idega.manager']['save']}"/>
					</wf:container>
                </f:facet>
			</wf:wfblock>
		</h:form>
		</ws:page>
    </f:view>
</jsp:root>

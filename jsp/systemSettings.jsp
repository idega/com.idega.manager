<jsp:root version="1.2" 
	xmlns:f="http://java.sun.com/jsf/core" 
	xmlns:h="http://java.sun.com/jsf/html" 
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:ws="http://xmlns.idega.com/com.idega.workspace"
	xmlns:wf="http://xmlns.idega.com/com.idega.webface"
	xmlns:c="http://xmlns.idega.com/com.idega.content"
	xmlns:x="http://myfaces.apache.org/tomahawk">
    <jsp:directive.page contentType="text/html"/>
    <f:view>
		<ws:page id="systemsettingspage" javascripturls="/dwr/engine.js,
        			/dwr/interface/ThemesEngine.js,
        			/idegaweb/bundles/com.idega.content.bundle/resources/javascript/ThemesHelper.js,
        			/idegaweb/bundles/com.idega.content.bundle/resources/javascript/SiteManagerHelper.js"
        			 stylesheeturls="/idegaweb/bundles/com.idega.manager.bundle/resources/style/manager.css">
		<h:form id="systemsettingsform" >
			<x:div id="siteInfoContainer" forceId="true">
				<c:SiteInfo id="siteInfo"></c:SiteInfo>
			</x:div>
			<f:verbatim><script type="text/javascript">setActiveLanguage();</script></f:verbatim>
		</h:form>
		</ws:page>
    </f:view>
</jsp:root>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="<c:choose><c:when test="${not empty language}">${language}</c:when><c:otherwise>en</c:otherwise></c:choose>" dir="<c:choose><c:when test="${not empty direction}">${direction}</c:when><c:otherwise>ltr</c:otherwise></c:choose>">
<head>
  <title>${title}</title>
  <meta name="_csrf" content="${_csrf.token}" />
  <meta name="_csrf_header" content="${_csrf.headerName}" />
  <meta name="_csrf_parameter" content="${_csrf.parameterName}" />
  ${metatags}
    <c:if test="${not empty pageResources.HeadTag}">
    	<c:forEach var="resource" items="${pageResources.HeadTag}">
    		<c:choose>
    			<c:when test="${resource.type == 'styles'}">
    				<spring:url value="${resource.url}" var="csspath"/>
   		  			<link type="text/css" href="${csspath}" rel="stylesheet">
    			</c:when>
    			<c:when test="${resource.type == 'scripts'}">
    				<spring:url value="${resource.url}" var="jspath"/>
    				<script src="${jspath}"></script>
    			</c:when>
    			<c:when test="${resource.type == 'raw'}">
    				${resource.url}
    			</c:when>
    		</c:choose>	
    	</c:forEach>
    </c:if>    
</head>
<body class="font-open-sans" style="padding-top: 100px; padding-bottom: 70px">
<input type="hidden" id="coords-latitude" value=""> 
<input type="hidden" id="coords-longitude" value=""> 
${pageContent}
<c:if test="${not empty pageResources.Bottom}">
	<c:forEach var="resource" items="${pageResources.Bottom}">
		<c:choose>
			<c:when test="${resource.type == 'styles'}">
				<spring:url value="${resource.url}" var="csspath"/>
	  			<link href="${csspath}" rel="stylesheet">
			</c:when>
			<c:when test="${resource.type == 'scripts'}">
				<spring:url value="${resource.url}" var="jspath"/>
				<script src="${jspath}"></script>
			</c:when>
			<c:when test="${resource.type == 'raw'}">
				${resource.url}
			</c:when>
		</c:choose>	
	</c:forEach>
</c:if> 
</body>
</html>
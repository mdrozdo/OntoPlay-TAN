
(function(){
  var Services=function($http){
    
    var getIndividuals=function(className){
        return $http.get('/individuals/class/' + className).then(function(response){
            return response.data;
          
        });
    }
	
	var getProperties=function(className){
		 return $http.get('/properties/class/' + className).then(function(response){
            return response.data;
          
        });
	}
	
	var getOperators=function(propertyUri,isDescriptionOfIndividual){
		 return $http.get('/properties/operators/' + propertyUri+'/'+isDescriptionOfIndividual).then(function(response){
            return response.data;
          
        });
	}
	
	var getClasses=function(className){
		 return $http.get('/class/property/' + className).then(function(response){
            return response.data;
          
        });
	}
	
	var updateIndividual=function(data,individualName){
	
		var dataToBeSend={'conditionJson':data,'name':individualName};
			 return $http.post('/individuals/save', dataToBeSend).then(function(response){
            return response.data;
          
        });
	}
	
	var getAnnotationProperties=function(componentUri){
		return $http.get('/annotationProperties/get/'+encodeURIComponent(componentUri)).then(function(response){
			return response.data;
			
		});
		
	}
	
	var isAddIndividual=function(){
		if(window.location.pathname.startsWith("/addClassExpression"))
			return false;
		return true;
	}
    
 
    
    return {
      getIndividuals:getIndividuals,
	  getProperties:getProperties,
	  getOperators:getOperators,
	  getClasses:getClasses,
	  update:updateIndividual,
	  getAnnotationProperties:getAnnotationProperties,
	  isAddIndividual:isAddIndividual
    };
  }
   var app = angular.module('Ontoplay');
   app.factory("Services",Services);
  
}());
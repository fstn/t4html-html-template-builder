angular.module("app").config(["$translateProvider",function($translateProvider){

    var en_translations = {
        "language" : "Selected Language English",
        "greeting" : "Welcome Dinesh"
    }

    var sp_translations = {
        "language" : "Selected Language Spanish",
        "greeting" : "Bienvenida Dinesh"
    }

    $translateProvider.translations('en',en_translations);

    $translateProvider.translations('sp',sp_translations);

    $translateProvider.preferredLanguage('en');

}]);

angular.module("app").controller("translateController" ,["$scope","$translate",function($scope,$translate){
    $scope.changeLanguage = function(lang){
        $translate.use(lang);
    }
}]);



angular.module("app").controller("ImputationController",["$scope","$rootScope","$translate",function($scope,$rootScope,$translate){
    $rootScope.invoiceData={"footer":[{"totalAmount":1387.09,"totalNetAmount":6935.44,"vatRate":0.2}],"header":{"number":"20160101_0001_00003","totalAmount":8322.53,"supplierId":"1008","clientId":"10001","totalNetAmount":6935.44,"supplierSiteId":"FR4458","currency":"EUR","scanDate":1456225141},"id":"20160101_0001_0003","body":[{"totalAmount":3768.99,"quantity":3,"netAmount":1256.33,"vatRate":0.2,"vatId":"FR1548798","productReference":"0000001"},{"totalAmount":2696.97,"quantity":3,"netAmount":898.99,"vatRate":0.2,"vatId":"FR1548798","productReference":"0000002"},{"totalAmount":469.48,"quantity":1,"netAmount":469.48,"vatRate":0.2,"vatId":"FR1000001","productReference":"0000003"}]}
}]);
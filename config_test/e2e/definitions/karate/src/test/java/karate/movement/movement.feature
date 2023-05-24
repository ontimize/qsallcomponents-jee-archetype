Feature: sample karate test script for Movement

  Background:
    * def urlBase = 'http://localhost:8080/qsallcomponents-jee/movements'
    * def getAuth =
    """
    function(creds) {
        var temp = creds.username + ':' + creds.password;
        var Base64 = Java.type('java.util.Base64');
        var encoded = Base64.getEncoder().encodeToString(temp.toString().getBytes());
        return 'Basic ' + encoded;
    }
    """
    * header Authorization = getAuth({username: 'demo', password: 'demouser'})



  Scenario:Basic GET
    Given url urlBase + '/movement?columns=MOVEMENTID,ACCOUNTID,MOVEMENT,MOVEMENTTYPEID,CONCEPT'
    When method GET
    Then status 200
    And def authToken = response
    And  match $..MOVEMENTID contains '#notnull'
    And  match $..MOVEMENTTYPEID contains '#notnull'
    And  match $..ACCOUNTID  == '#present'
    And  match $..CONCEPT contains 'First movement'

  Scenario: Testing a POST endpoint with request body
    * def movement =

  """
  {
    "data": {
        "MOVEMENTID":77338,
        "MOVEMENTTYPEID":3,
        "ACCOUNTID": 19378,
        "CONCEPT":"First movementKarate",
        "MOVEMENT": 1313.13
        }
  }
  """
    Given url urlBase + '/movement'
    And request movement
    When method post
    Then status 200
    And match $..MOVEMENTID == '#present'
    * print 'postmovement-> ', movement


  Scenario:GET to check the POST
    Given url urlBase + '/movement?columns=MOVEMENTID,ACCOUNTID,MOVEMENT,MOVEMENTTYPEID,CONCEPT'
    When method GET
    Then status 200
    And def authToken = response
    And  match $..MOVEMENTID contains '#notnull'
    And  match $..MOVEMENTTYPEID contains '#notnull'
    And  match $..ACCOUNTID  == '#present'

  Scenario: Testing a PUT endpoint with request body
    * def newPostBodyForPut =
     """
  {
    "filter" :{
		"MOVEMENTID": 80527
	},
    "data": {
              "CONCEPT":"movementPut"
    }}
    """

    Given url urlBase + '/movement/'
    And request newPostBodyForPut
    When method put
    Then status 200
    And print 'newPostBodyForPut-> ', newPostBodyForPut


  Scenario: Delete request
    * def deleteId =
      """
  {
   "filter" :{
		"MOVEMENTID": 80527
	}
  }
    """
    Given url urlBase + '/movement/'
    And request deleteId
    When method DELETE
    Then status 200



  Scenario: Testing a PAGINATION QUERY endpoint with request body
    * def movementPQ =

  """
      {
    "filter": {},
    "columns": [
        "MOVEMENTID",
        "ACCOUNTID",
        "MOVEMENT",
        "MOVEMENTTYPEID",
        "CONCEPT",
        "DATE_"
        ],
    "offset": 0,
    "pageSize": 10,
    "orderBy": []
}
  """

    Given url urlBase + '/movement/advancedsearch'
    And request movementPQ
    When method post
    Then status 200
    And match $..OFFICEID == '#present'
    * print 'postmovementPQ-> ', movementPQ


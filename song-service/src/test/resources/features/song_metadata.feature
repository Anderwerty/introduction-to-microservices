Feature: Song Metadata REST API
  The API allows creating, retrieving, and deleting song metadata.
  Validation rules ensure that input data is properly formatted.

  Background:
    Given the application is running

  # ---------------------------------------------------------------------------
  # 1. Successful creation of song metadata
  # ---------------------------------------------------------------------------
  Scenario: Successfully create song metadata
    Given I have valid song metadata
    When I send a POST request to "/songs"
    Then the response status should be 200
    And the response should contain a valid ID

  # ---------------------------------------------------------------------------
  # 2. Invalid creation (bad duration format)
  # ---------------------------------------------------------------------------
  Scenario: Fail to create metadata with invalid duration
    Given I have song metadata with invalid duration
    When I send a POST request to "/songs"
    Then the response status should be 400

  # ---------------------------------------------------------------------------
  # 3. Invalid creation (missing required field)
  # ---------------------------------------------------------------------------
  Scenario: Fail to create metadata with missing song name
    Given I have song metadata with missing song name
    When I send a POST request to "/songs"
    Then the response status should be 400
    And the response should contain "Song name is required"

  # ---------------------------------------------------------------------------
  # 4. Successfully retrieve existing song metadata
  # ---------------------------------------------------------------------------
  Scenario: Successfully retrieve song metadata
    Given I have valid song metadata
    And I send a POST request to "/songs"
    When I send a GET request to "/songs/1"
    Then the response status should be 200
    And the response should contain json:
    """
    {
      "id":1,
      "name":"Обійми",
      "artist":"гурт Oкеан Ельзи, вокаліст Святослав Вакарчук",
      "album":"Земля",
      "duration":"3:46",
      "year":"2013"
    }
    """

  # ---------------------------------------------------------------------------
  # 5. Fail to retrieve non-existing song metadata
  # ---------------------------------------------------------------------------
  Scenario: Fail to retrieve non-existing song metadata
    When I send a GET request to "/songs/9999"
    Then the response status should be 404

  # ---------------------------------------------------------------------------
  # 6. Successfully delete multiple songs
  # ---------------------------------------------------------------------------
  Scenario: Successfully delete multiple song metadata records
    Given I have multiple existing songs
    When I send a DELETE request to "/songs?id=1,2"
    Then the response status should be 200
    And the response should contain json:
    """
    {
      "ids":[1,2]
    }
    """

  # ---------------------------------------------------------------------------
  # 7. Fail to delete with invalid ID format
  # ---------------------------------------------------------------------------
  Scenario: Fail to delete with invalid IDs format
    When I send a DELETE request to "/songs?id=abc,123"
    Then the response status should be 400

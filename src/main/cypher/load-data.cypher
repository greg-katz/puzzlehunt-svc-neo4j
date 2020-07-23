// Deletes all nodes and relationships
MATCH (n)
DETACH DELETE n;

// Load All The Things (nodes and their relationships)!
CREATE
(dash11:Hunt {
  uuid: "2c2b2203-ced6-452d-b4dc-9ee601d304e1",
  name: "DASH 11",
  start: datetime('2015-06-24T12:50:35.556+0100'),
  end: datetime('2015-06-24T17:50:35.556+0100')
}),

(lungs:Puzzle {
  uuid: "9e03b12a-dc65-485f-af28-9c5251a5c6f5",
  name: "Lungs",
  answer: "oxygen",
  par: 30,
  points: 50
}),
(dash11)-[:HAS {order: 1 }]->(lungs),

(lungHint1:Hint {
  uuid: "5758004b-c6de-4506-a461-747eb39abba2",
  unlockMins: 10,
  cost: 5,
  text: "lung hint 1 text"
}),
(lungs)-[:HAS {order: 1 }]->(lungHint1),

(lungHint2:Hint {
  uuid: "a567360f-3a54-4d75-94a3-0b7177e8e691",
  unlockMins: 30,
  cost: 20,
  text: "lung hint 2 text"
}),
(lungs)-[:HAS {order: 2 }]->(lungHint2),

(lungPartialSolution1:PartialSolution {
  uuid: "efb73a24-a4be-4755-a7b1-a6250727eaaa",
  solution: "OXY",
  response: "Keep typing"
}),
(lungs)-[:HAS ]->(lungPartialSolution1),

(lungPartialSolution2:PartialSolution {
  uuid: "b334b947-1296-48a1-8a34-dbf7a2cf4918",
  solution: "O2",
  response: "Incomplete!"
}),
(lungs)-[:HAS ]->(lungPartialSolution2),

(heart:Puzzle {
  uuid: "17a67d77-c093-4d0d-9e25-be2ba4f52419",
  name: "Heart",
  answer: "HEART!",
  par: 20,
  points: 30
}),
(dash11)-[:HAS {order: 2 }]->(heart),

(heartHint1:Hint {
  uuid: "b6cd5628-599e-44dc-9f22-bc2d014cceb8",
  unlockMins: 15,
  cost: 10,
  text: "heart hint 1 text"
}),
(heart)-[:HAS {order: 1 }]->(heartHint1),

(heartHint2:Hint {
  uuid: "ee51445c-0bd8-4d93-bd85-ccb28d852537",
  unlockMins: 40,
  cost: 50,
  text: "heart hint 2 text"
}),
(heart)-[:HAS {order: 2 }]->(heartHint2),

(heartPartialSolution1:PartialSolution {
  uuid: "a2b82cb1-5117-46fe-988d-822dc1f79da6",
  solution: "HEART",
  response: "Not enough enthusiasm"
}),
(heart)-[:HAS ]->(heartPartialSolution1),

(heartPartialSolution2:PartialSolution {
  uuid: "62aff225-bee9-4fa8-a416-6bba51e2f87b",
  solution: "OXYGEN",
  response: "That's lungs, pay attention!"
}),
(heart)-[:HAS ]->(heartPartialSolution2),

(team1:Team {
  uuid: "66af7be3-7240-48a9-9e19-f2ff0e885910",
  name: "Team 1"
}),

(player1:Person {
  uuid: "d80d2554-9e48-47b4-a52b-e8b4ba22cc16",
  name: "Player 1"
}),
(player1)-[:MEMBER_OF]->(team1),

(player2:Person {
  uuid: "ddf34617-df25-465f-bb29-4d0424373033",
  name: "Player 2"
}),
(player2)-[:MEMBER_OF]->(team1),
(player2)-[:CAPTAIN_OF]->(team1),

(team1)-
  [:PLAYED]
->(dash11),

(team1)-
  [:SOLVED {uuid:"b072e551-4f7f-4cab-bd1d-acffeef4dabe", start: datetime('2015-06-24T12:52:01.001+0100'), end: datetime('2015-06-24T13:28:55.821+0100'), points: 11}]
->(lungs),

(team1)-
  [:SOLVED {uuid: "214941d3-98d8-4378-b9f1-c69490e59e26", start: datetime('2015-06-24T13:40:32.987+0100'), end: datetime('2015-06-24T15:52:01.001+0100'), points: 5}]
->(heart),

(team1)-[:BOUGHT]->(lungHint1),

(team2:Team {
  uuid: "4090c00c-1219-4cf3-877e-146f345ec498",
  name: "Team 2"
}),

(player3:Person {
  uuid: "9434d65c-a693-4dac-95fd-5496ebd32650",
  name: "Player 3"
}),
(player3)-[:MEMBER_OF]->(team2),
(player3)-[:CAPTAIN_OF]->(team2),

(player4:Person {
  uuid: "5efd7a47-98f7-4c4d-87e9-ba7434c4afa6",
  name: "Player 4"
}),
(player4)-[:MEMBER_OF]->(team2),

(team2)-
  [:PLAYED]
->(dash11),

(team2)-
  [:SOLVED {
    uuid: "34e52114-9ca9-4981-9b9c-99ca1ab8dd0e",
    start: datetime('2015-06-24T12:52:01.002+0100'),
    end: datetime('2015-06-24T13:42:41.722+0100'),
    points: 12}]
->(lungs),

(team2)-
  [:SOLVED {uuid: "7a5a4094-a751-4b72-908c-22a2244a4881", start: datetime('2015-06-24T14:02:28.002+0100')}]
->(heart),

(team2)-[:BOUGHT]->(lungHint1),
(team2)-[:BOUGHT]->(lungHint2)

;

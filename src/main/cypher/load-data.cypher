// Load All The Things (nodes and their relationships)!
CREATE
(dash11:Hunt {
  name: "DASH 11",
  start: datetime('2015-06-24T12:50:35.556+0100'),
  end: datetime('2015-06-24T17:50:35.556+0100')
}),

(lungs:Puzzle {
  name: "Lungs",
  answer: "oxygen",
  par: 30,
  points: 50
}),
(dash11)-[:HAS {order: 1 }]->(lungs),

(lungHint1:Hint {
unlockMins: 10,
cost: 5,
text: "lung hint 1 text"`
}),
(lungs)-[:HAS {order: 1 }]->(lungHint1),

(lungHint2:Hint {
unlockMins: 30,
cost: 20,
text: "lung hint 2 text"
}),
(lungs)-[:HAS {order: 2 }]->(lungHint2),

(lungPartialSolution1:PartialSolution {
solution: "OXY",
response: "Keep typing"
}),
(lungs)-[:HAS ]->(lungPartialSolution1),

(lungPartialSolution2:PartialSolution {
solution: "O2",
response: "Incomplete!"
}),
(lungs)-[:HAS ]->(lungPartialSolution2),

(heart:Puzzle {
  name: "Heart",
  answer: "HEART!",
  par: 20,
  points: 30
}),
(dash11)-[:HAS {order: 2 }]->(heart),

(heartHint1:Hint {
unlockMins: 15,
cost: 10,
text: "heart hint 1 text"
}),
(heart)-[:HAS {order: 1 }]->(heartHint1),

(heartHint2:Hint {
unlockMins: 40,
cost: 50,
text: "heart hint 2 text"
}),
(heart)-[:HAS {order: 2 }]->(heartHint2),

(heartPartialSolution1:PartialSolution {
solution: "HEART",
response: "Not enough enthusiasm"
}),
(heart)-[:HAS ]->(heartPartialSolution1),

(heartPartialSolution2:PartialSolution {
solution: "OXYGEN",
response: "That's lungs, pay attention!"
}),
(heart)-[:HAS ]->(heartPartialSolution2),

(team1:Team {
  name: "Team 1"
}),

(player1:Person {
  name: "Player 1"
}),
(player1)-[:MEMBER_OF]->(team1),

(player2:Person {
  name: "Player 2"
}),
(player2)-[:MEMBER_OF]->(team1),
(player2)-[:CAPTAIN_OF]->(team1),

(team1)-
  [:PLAYED {start: localtime('12:52:01.001'), end: localtime('17:52:01.001')}]
->(dash11),

(team1)-
  [:SOLVED {start: localtime('12:52:01.001'), end: localtime('13:52:01.001'), points: 11}]
->(lungs),

(team1)-[:BOUGHT]->(lungHint1),

(team2:Team {
  name: "Team 2"
}),

(player3:Person {
  name: "Player 3"
}),
(player3)-[:MEMBER_OF]->(team2),
(player3)-[:CAPTAIN_OF]->(team2),

(player4:Person {
  name: "Player 4"
}),
(player4)-[:MEMBER_OF]->(team2),

(team2)-
  [:PLAYED {start: localtime('12:52:02.002')}]
->(dash11),

(team2)-
  [:SOLVED {
    start: localtime('12:52:01.001'),
    end: localtime('13:52:01.001'),
    points: 12}]
->(lungs),

(team2)-
  [:SOLVED {start: localtime('12:52:01.001')}]
->(heart),

(team2)-[:BOUGHT]->(lungHint1),
(team2)-[:BOUGHT]->(lungHint2)

;

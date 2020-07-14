match (hunt:Hunt)<-[played:PLAYED]-(team:Team)-[solved:SOLVED]->(puzzle:Puzzle)
with hunt.name as huntName, team, solved
where huntName = 'DASH 11'
call {
  with huntName
  match (hunt:Hunt)-[:HAS]->(puzzle:Puzzle)
  where hunt.name = huntName
  return count(puzzle) as totalPuzzles
}
return
  team.name as teamName,
  count(solved.end) = totalPuzzles as finishedHunt,
  sum(solved.points) as score,
  duration.between(min(solved.start), max(solved.end)) as huntDuration,
  sum(duration.between(solved.start, solved.end)) as solvingDuration
order by score desc
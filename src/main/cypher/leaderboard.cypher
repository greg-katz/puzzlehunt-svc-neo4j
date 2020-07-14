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
  team.name as name,
  count(solved.end) = totalPuzzles as finished,
  sum(solved.points) as score,
  sum(duration.between(solved.start, solved.end)) as time
order by score desc
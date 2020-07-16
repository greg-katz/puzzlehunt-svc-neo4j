// TODO: Most of these name constraints are too restrictive - a team's name needn't be unique across hunts, for
// example. For now, these too-tight constraints will make dev easier.

CREATE CONSTRAINT hint_uuid_unique ON (hint:Hint) ASSERT hint.uuid IS UNIQUE;

CREATE CONSTRAINT hunt_uuid_unique ON (hunt:Hunt) ASSERT hunt.uuid IS UNIQUE;
CREATE CONSTRAINT hunt_name_unique ON (hunt:Hunt) ASSERT hunt.name IS UNIQUE;

CREATE CONSTRAINT person_uuid_unique ON (person:Person) ASSERT person.uuid IS UNIQUE;
CREATE CONSTRAINT person_name_unique ON (person:Person) ASSERT person.name IS UNIQUE;

CREATE CONSTRAINT puzzle_uuid_unique ON (puzzle:Puzzle) ASSERT puzzle.uuid IS UNIQUE;
CREATE CONSTRAINT puzzle_name_unique ON (puzzle:Puzzle) ASSERT puzzle.name IS UNIQUE;

CREATE CONSTRAINT team_uuid_unique ON (team:Team) ASSERT team.uuid IS UNIQUE;
CREATE CONSTRAINT team_name_unique ON (team:Team) ASSERT team.name IS UNIQUE;

/* In case you need to recreate the constraints, run these drop statements:
DROP CONSTRAINT hint_uuid_unique;
DROP CONSTRAINT hunt_uuid_unique;
DROP CONSTRAINT hunt_name_unique;
DROP CONSTRAINT person_uuid_unique;
DROP CONSTRAINT person_name_unique;
DROP CONSTRAINT puzzle_uuid_unique;
DROP CONSTRAINT puzzle_name_unique;
DROP CONSTRAINT team_uuid_unique;
DROP CONSTRAINT team_name_unique;
*/
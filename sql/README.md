# Introduction
This SQL training project is designed to provide a comprehensive practice platform for mastering SQL clauses and database management concepts. 
The project includes a detailed learning section covering essential topics such as normalization, data modeling, entity relationships, and other fundamental database principles. 
Additionally, the project includes practical exercises aimed at reinforcing the theoretical knowledge gained. 
It's a good opportunity to practice various SQL queries, enhancing proficiency in data manipulation and retrieval. 
This project serves as a robust foundation for developing advanced SQL skills, ensuring a thorough understanding of both the theoretical and practical aspects of database management.


# SQL Queries
#### Table Setup (DDL)

This table stores information about the club's members. Each member has a unique identifier (memid), 
along with personal details such as their surname, first name, address, zipcode, and telephone number.
Additionally, the recommendedby field is a foreign key that references the memid 
of another member who recommended the new member, enabling the tracking of member recommendations.
```sql
CREATE TABLE IF NOT EXISTS cd.members (
	memid integer not null,
	surname varchar(200) not null,
	firstname varchar(200) not null,
	address varchar(300) not null,
	zipcode integer not null, 
	telephone varchar(20) not null,
	joinddate timestamp not null,
	recommendedby integer,
	CONSTRAINTS members_pk PRIMARY KEY (memId),
	CONSTRAINTS members_fk FOREIGN KEY (recommendedby) REFERENCES cd.members(memid)
);
```

This table contains details about the club's facilities. Each facility has a unique identifier (facid), 
and fields to store its name, member usage cost (membercost), guest usage cost (guestcost), 
initial setup cost (initialoutlay), and monthly maintenance cost (monthlymaintenance).

```sql
CREATE TABLE IF NOT EXISTS cd.facilities (
	facid integer not nul,
	name varchar(100) not null,
	membercost numeric not null,
	gestcost numeric not null,
	initialoutlay numeric not null,
	monthlymaintenance numeric not null,
	CONSTRAINTS facilities_pk PRIMARY KEY (facid)
);
```

This table records the bookings made by members for using the club's facilities. 
Each booking has a unique identifier (bookid). The memid field is a foreign key referencing the member 
who made the booking, while the facid field is a foreign key referencing the booked facility.

```sql
CREATE TABLE IF NOT EXISTS cd.bookings (
	bookid integer not null,
	memid integer not null,
	facid integer not null,
	starttime timestamp not null,
	slots integer not null,
	CONSTRAINTS bookings_pk PRIMARY KEY (bookid),
	CONSTRAINTS bookings_members_fk FOREIGN KEY (memid) REFERENCES cd.members(memid),
	CONSTRAINTS bookings_facilities_fk_fk FOREIGN KEY (facId) REFERENCES cd.facilities(facId)
);
```

#### Modifying data

###### Query 1 : Insert a new facility
This query inserts a new record into the cd.facilities table. 
It adds a new facility with the following details: facid of 9, name as 'Spa', membercost of 20, 
guestcost of 30, initialoutlay of 100000, and monthlymaintenance of 800.
```sql
insert into cd.facilities(
	facid, name, membercost, guestcost,
	initialoutlay, monthlymaintenance
)
values
	(9, 'Spa', 20, 30, 100000, 800);
```

###### Query 2 : Insert a new facility with dynamic id
This query inserts a new record into the cd.facilities table, 
dynamically assigning a unique facid by selecting the current maximum facid and incrementing it by 1.
```sql
insert into cd.facilities(
    facid, name, membercost, guestcost,
    initialoutlay, monthlymaintenance
)
values
    (
        (
            select
                max(facid)+ 1
            from
                cd.facilities
        ),
        'Spa',
        20,
        30,
        100000,
        800
    );
```

###### Query 3 : Update faicility initial outlay
This query updates the initialoutlay for the facility with a facid of 1 in the cd.facilities table. 
It sets the initialoutlay to 10000.
```sql
update 
  cd.facilities 
set 
  initialoutlay = 10000 
where 
  facid = 1;
```

###### Query 4 : Update facility cost based on another facility
This query uses a subquery to fetch the costs from the reference facility and applies a 10% increase.
```sql
update 
  cd.facilities f1 
set 
  membercost = f2.membercost * 1.1, 
  guestcost = f2.guestcost * 1.1 
from 
  (
    select 
      * 
    from 
      cd.facilities 
    where 
      facid = 0
  ) f2 
where 
  f1.facid = 1;
```
###### Query 5 : Delete all the bookings
This query removes all records from the cd.bookings table.
```sql
delete from 
  cd.bookings;
```
###### Query 6 : Delete a member
This query deletes the record from the cd.members table where the memid is 37.
```sql
delete from 
  cd.members 
where 
  memid = 37;
```

#### Basics
###### Query 1 : Retrieve facilities with cost efficiency
This query selects facilities where the membership cost is comparatively 
lower than their maintenance cost.
```sql
SELECT 
  facid, 
  name, 
  membercost, 
  monthlymaintenance 
FROM 
  cd.facilities 
WHERE 
  membercost < monthlymaintenance / 50 
  AND membercost != 0;
```

###### Query 2 : Retrieve 'tennis' facilities
This query retrieves all columns (*) from the cd.facilities 
table where the facility name (name) contains the substring 'Tennis'.
The LIKE operator with % wildcard characters allows for flexible pattern matching, 
making it useful for searching and filtering facilities.
```sql
select 
  * 
from 
  cd.facilities 
where 
  name like '%Tennis%';
```

###### Query 3 : Retrieve facilities by id
This query selects all columns (*) from the cd.facilities table where the facid is either 1 or 5. 
The IN operator is used to specify multiple values for filtering, 
allowing retrieval of specific facilities identified by their unique IDs.
```sql
select 
  * 
from 
  cd.facilities 
where 
  facid in (1, 5);
```

###### Query 4 : Retrieve members who joined after a specific date
This query retrieves details of members who have joined the club from the specified date onward
```sql
select 
  memid, 
  surname, 
  firstname, 
  joindate 
from 
  cd.members 
where 
  joindate >= '2012-09-01';
```
###### Query 5 : Union of member and facility names
This query performs a UNION operation between the surname column from the cd.members table and the name column from the cd.facilities table. 
The UNION operator combines the results of the two SELECT statements, removing duplicates by default.
```sql
select 
  surname 
from 
  cd.members 
union 
select 
  name 
from 
  cd.facilities;
```
#### Join 

###### Query 1 : Retrieve booking start times for a specific member
This query uses a JOIN operation between cd.bookings and cd.members tables on memid to link bookings with member details.
```sql
select 
  starttime 
from 
  cd.bookings b 
  join cd.members m on b.memid = m.memid 
where 
  firstname = 'David' 
  and surname = 'Farrell';
```

###### Query 2 : Retrieve tennis court bookings on a specific date
This query retrieves the starttime and name of facilities 
from the cd.facilities table that are bookings for tennis courts.
It joins cd.bookings with cd.facilities on facid. 
The WHERE clause filters results to include bookings on September 21, 2012. 
Results are sorted first by starttime and then by name.
```sql
select 
  starttime as start, 
  name 
from 
  cd.facilities f 
  join cd.bookings b on b.facid = f.facid 
where 
  starttime >= '2012-09-21' 
  and starttime < '2012-09-22' 
  and name like 'Tennis Court%' 
order by 
  starttime, 
  name;
```

###### Query 3 : Retrieve member and recommender names
This query selects the first name and surname of two sets of members from the cd.members table. 
It performs a LEFT JOIN between cd.members (m1) and itself (m2) based on the recommendedby column.
The LEFT JOIN ensures all records from m1 (members) are included in the result set, 
regardless of whether there is a match in m2 (recommendations).
Results are sorted first by memsname and then by memfname.
```sql
select 
  m1.firstname as memfname, 
  m1.surname as memsname, 
  m2.firstname as recfname, 
  m2.surname as recsname 
from 
  cd.members m1 
  left join cd.members m2 on m1.recommendedby = m2.memid 
order by 
  memsname, 
  memfname;
```

###### Query 4 : Retrieve only recommenders
This query selects distinct the first name and surname of the member who have recommended
another member. It does the same thing that the previous query, but only display recommenders.
Results are sorted first by surname and then by first name.
```sql
select 
  distinct m2.firstname, 
  m2.surname 
from 
  cd.members m1 
  join cd.members m2 on m1.recommendedby = m2.memid 
order by 
  m2.surname, 
  m2.firstname;
```

###### Query 5 : Retrieve member and recommender names (by using a subquery)
This query does the same thing that the third query, but instead of using a left join
operation, it uses a subquery. Results are sorted by member.
```sql
select 
  distinct m1.firstname || ' ' || m1.surname as member, 
  (
    select 
      m2.firstname || ' ' || m2.surname as recommender 
    from 
      cd.members m2 
    where 
      m2.memid = m1.recommendedby
  ) 
from 
  cd.members m1 
order by 
  member;
```
#### Aggregation

###### Query 1 : Count of recommendations per recommender
This query calculates the number of recommendations (COUNT(*)) made by each recommender 
in the cd.members table. It uses GROUP BY recommendedby to group the results by the recommender's ID. 
The HAVING recommendedby IS NOT NULL clause ensures that only non-null recommender IDs are included in the results.
Results are sorted by recommenders.
```sql
select 
  recommendedby, 
  count(*) as count 
from 
  cd.members 
group by 
  recommendedby 
having 
  recommendedby is not null 
order by 
  recommendedby;
```

###### Query 2 : Total slots booked per facility
This query calculates the total number of slots (SUM(slots)) booked for each facility (facid) in the cd.bookings table. 
The GROUP BY facid clause groups bookings by facility ID, aggregating the total number of slots booked per facility.
Results are sorted by facility.
```sql
select
    facid,
    sum(slots) as "Total Slots"
from
    cd.bookings
group by
    facid
order by
    facid;
```

###### Query 3 : Total slots booked per facility on a specific period
This query calculates the total number of slots (SUM(slots)) booked for each facility (facid) during September 2012.
The GROUP BY facid clause groups bookings by facility ID, aggregating the total slots booked per facility for the specified period.
Results are ordered based on the total number of slots booked.
```sql
select 
  facid, 
  sum(slots) as "Total Slots" 
from 
  cd.bookings 
where 
  starttime >= '2012-09-01' 
  and starttime < '2012-10-01' 
group by 
  facid 
order by 
  sum(slots);
```
###### Query 4 : Total slots booked per facility by month on a specific year
This query calculates the total number of slots (SUM(slots)) booked for each facility by month in the year 2012. 
The EXTRACT(month FROM starttime) function extracts the month from the starttime column, grouping bookings by facility and month. 
The WHERE EXTRACT(year FROM starttime) = 2012 clause filters bookings to include only those from the year 2012. 
Results are ordered first by facility ID and then by month.
```sql
select 
  facid, 
  extract(
    month 
    from 
      starttime
  ) as month, 
  sum(slots) as "Total Slots" 
from 
  cd.bookings 
where 
  extract(
    year 
    from 
      starttime
  ) = 2012 
group by 
  facid, 
  month 
order by 
  facid, 
  month;
```
###### Query 5 : Count of unique members making bookings
This query calculates the count of unique member IDs who have made bookings in the cd.bookings table. 
The COUNT(DISTINCT memid) function counts only distinct (unique) values of memid, 
ensuring each member is counted only once regardless of the number of bookings they have made.
```sql
select 
  count(distinct memid) 
from 
  cd.bookings;
```

###### Query 6 : Earliest booking start time per member since a specific date
This query retrieves the surname, first name, unique member ID, and the earliest booking start time (MIN(starttime)) 
made by each member since September 2012. It joins the cd.members table with the cd.bookings table on memid to associate members with their bookings. 
The GROUP BY m.memid clause ensures each member's results are aggregated by their unique ID, and ORDER BY m.memid arranges the output in ascending order of member IDs.
```sql
select 
  surname, 
  firstname, 
  m.memid, 
  min(starttime) as starttime 
from 
  cd.members m 
  join cd.bookings b on m.memid = b.memid 
where 
  starttime >= '2012-09-01' 
group by 
  m.memid 
order by 
  m.memid;
```

###### Query 7 : Ranking members by join date
This query assigns a sequential row number to each member in the cd.members table based on their join date. 
The ROW_NUMBER() function is a window function that generates a unique number for each row in the result set according to the specified ordering.
The result set includes columns for first name and surname of each member, ordered by their join date.
```sql
select
    row_number() over(),
    firstname,
    surname
from
    cd.members
order by
    joindate;
```

###### Query 8 : Facility with the highest total slots booked
This query identifies the facility with the highest total number of booking slots from the cd.bookings table. 
The inner query calculates the sum of slots booked (SUM(slots)) for each facility and assigns a rank (RANK()) based on the descending order of total slots booked.
The outer query selects facilities where the rank is equal to 1, indicating the facility with the most slots booked.
```sql
select 
  facid, 
  total 
from 
  (
    select 
      facid, 
      sum(slots) as total, 
      rank() over(
        order by 
          sum(slots) desc
      ) as rank 
    from 
      cd.bookings 
    group by 
      facid
  ) as ranked 
where 
  rank = 1;
```

#### String
###### Query 1 : Concatenated member names
This query concatenates the surname and firstname columns of members from the cd.members table 
into a single column named name, formatted as "Surname, Firstname".
```sql
select 
  (surname || ', ' || firstname) as name 
from 
  cd.members;
```

###### Query 2 : Members with parentheses in telephone numbers
This query retrieves memid and telephone from the cd.members table, 
filtering records where the telephone column matches a regular expression pattern [()]. 
The ~ operator with the regular expression [()] checks if the telephone number contains parentheses. 
It orders the results by memid.
```sql
select 
  memid, 
  telephone 
from 
  cd.members 
where 
  telephone ~ '[()]' 
order by 
  memid;
```

###### Query 3 : Count of members by first letter of surname
This query calculates the count of members (COUNT(*)) grouped by the first letter (SUBSTR(mems.surname, 1, 1)) of their surname.
The SUBSTR function extracts the first character of each member's surname to categorize members by surname initial. 
Results are ordered alphabetically by first letter of surname.
```sql
select 
  substr (mems.surname, 1, 1) as letter, 
  count(*) as count 
from 
  cd.members mems 
group by 
  letter 
order by 
  letter;
```


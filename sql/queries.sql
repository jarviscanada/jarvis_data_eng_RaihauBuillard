-- Modifying data
-- 1
insert into cd.facilities(
    facid, name, membercost, guestcost,
    initialoutlay, monthlymaintenance
)
values
    (9, 'Spa', 20, 30, 100000, 800);

-- 2
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


-- 3
update
    cd.facilities
set
    initialoutlay = 10000
where
    facid = 1;


-- 4
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


-- 5
delete from
    cd.bookings;


-- 6
delete from
    cd.members
where
    memid = 37;


-- Basics
-- 1
select
    facid,
    name,
    membercost,
    monthlymaintenance
from
    cd.facilities
where
    membercost < monthlymaintenance / 50
  and membercost != 0;

-- 2
select
    *
from
    cd.facilities
where
    name like '%Tennis%';


-- 3
select
    *
from
    cd.facilities
where
    facid in (1, 5);

-- 4
select
    memid,
    surname,
    firstname,
    joindate
from
    cd.members
where
    joindate >= '2012-09-01';

-- 5
select
    surname
from
    cd.members
union
select
    name
from
    cd.facilities;

-- Joins
-- 1
select
    starttime
from
    cd.bookings b
        join cd.members m on b.memid = m.memid
where
    firstname = 'David'
  and surname = 'Farrell';

-- 2
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

-- 3
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

-- 4
select
    distinct m2.firstname,
             m2.surname
from
    cd.members m1
        join cd.members m2 on m1.recommendedby = m2.memid
order by
    m2.surname,
    m2.firstname;


-- 5
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


-- Aggregation
-- 1
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

-- 2
select
    facid,
    sum(slots) as "Total Slots"
from
    cd.bookings
group by
    facid
order by
    facid;

-- 3
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


-- 4
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

-- 5
select
    count(distinct memid)
from
    cd.bookings;

-- 6
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

-- 7
select
    row_number() over(),
        firstname,
    surname
from
    cd.members
order by
    joindate;

-- 8
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

--String
--1
select
    (surname || ', ' || firstname) as name
from
    cd.members;

--2
select
    memid,
    telephone
from
    cd.members
where
    telephone ~ '[()]'
order by
    memid;

--3
select
    substr (mems.surname, 1, 1) as letter,
    count(*) as count
from
    cd.members mems
group by
    letter
order by
    letter;




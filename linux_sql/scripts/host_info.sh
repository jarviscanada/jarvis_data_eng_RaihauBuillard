#!/bin/sh

psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5


if [ "$#" -ne 5 ]; then
	echo "Illegal number of parameters"
	exit 1
fi

lscpu_out=$(lscpu)
cpu_info=$(cat /proc/cpuinfo)


hostname=$(hostname -f)
cpu_number=$(echo "$lscpu_out" | egrep "^CPU\(s\)" | awk '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_out"  | grep Architecture | awk '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_out"  | grep Model | awk '{for (i=3; i<=NF; i++) print $i}' | xargs)
cpu_mhz=$(echo "$cpu_info"  | grep MHz | head -1 | awk '{print $4}' | xargs)
l2_cache=$(echo "$lscpu_out"  | grep L2 | awk '{print $3}' | xargs)
total_mem=$(vmstat --unit M | tail -1 | awk '{print $4}' | xargs)
timestamp=$(date +"%Y-%m-%d %H:%M:%S")

insert_stmt="INSERT INTO host_info (hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, timestamp, total_mem) VALUES('$hostname', '$cpu_number', '$cpu_architecture', '$cpu_model', '$cpu_mhz', '$l2_cache', '$timestamp', '$total_mem');"

export PGPASSWORD=$psql_password
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"
exit $?


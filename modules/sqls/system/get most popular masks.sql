select object_name, count(*)
from BW_AMC.AMC_USER_SYSTEM_ACTIONS
group by object_name order by count(*) desc

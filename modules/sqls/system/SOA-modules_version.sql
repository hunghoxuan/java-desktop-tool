select hve_module_id, max(release_id) from
hve_release
where hve_module_id in (select hve_module_id from SYS_HVE_MODULES where available = '001')
group by hve_module_id
order by 1

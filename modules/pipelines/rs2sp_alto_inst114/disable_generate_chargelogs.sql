update sys_configuration set
config_value = 0
where institution_number = '00000114' and config_keyword = 'GenerateChargesLog';
commit;

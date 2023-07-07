REM BAK NONE
update bw3.wsm_configuration
 set config_value = '22050'
 where config_keyword = 'CommsPort';
COMMIT;
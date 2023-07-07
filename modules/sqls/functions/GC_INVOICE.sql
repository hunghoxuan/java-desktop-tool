procedure gc_invoice (
  param1 in varchar2,
  result out sys_refcursor
) is 
  cursor c1 is
    select *
    from sys_institution_licence;
begin
  open result for 'select * from sys_institution_licence';
end; 
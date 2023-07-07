function getMerchantBrand(p_clientNumber in varchar2, p_groupNumber in varchar2, p_institutionNumber in varchar2)
return varchar2 as
--
v_return varchar2(3);
--
begin
	select *
	into v_return
	from
	(
		Select brd.brand_id from view_cps_merchant_branding brd, cps_brand brn
		where brd.client_number = p_clientNumber
		and brd.group_number = p_groupNumber
		and brd.institution_number = p_institutionNumber
		and brd.brand_id = brn.brand_id
		order by brn.priority
	)
	where rownum = 1;
	--
	return v_return;
	--
	exception
	when no_data_found then
	return '000';
end getMerchantBrand;
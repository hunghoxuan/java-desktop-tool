PROCEDURE GenerateCard (p_card_org in varchar2,
                        p_product in varchar2,
                        p_acceptance_brand in varchar2 default null , 
                        p_paypass_enable in varchar2, 
                        p_country in varchar2, 
                        p_funding_source in varchar2 default null,
                        p_b2b_id in varchar2,
                        p_technology in varchar2,  
                        p_inst  in varchar2,
                        i_return out number )
is

     v_card_number varchar2(19);
     v_bin_aoe varchar2(6);
     v_bin_country varchar2(3);
begin
    i_return := '0';
    if p_card_org in ('002','020') then     v_card_number  := GenerateCardNumber.GenerateCardMC(p_product, p_acceptance_brand, p_paypass_enable, p_country, p_funding_source );
  elsif p_card_org = '003' then     v_card_number  := GenerateCardNumber.GenerateCardVisa(p_product, p_acceptance_brand, p_country, p_funding_source, p_b2b_id, p_technology,p_inst);
  elsif p_card_org = '006' then     v_card_number  := GenerateCardNumber.GenerateCardDiners(p_product,  p_country);
  elsif p_card_org = '025' then     v_card_number  := GenerateCardNumber.GenerateCardELO(p_product, p_acceptance_brand, p_funding_source, p_b2b_id);

            end if;
    i_return := v_card_number;
  exception
  when no_data_found then
    null;
end;

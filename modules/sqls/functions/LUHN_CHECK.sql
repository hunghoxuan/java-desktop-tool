function LUHN_CHECK(card_number varchar2)
return varchar2
is
     new_card        varchar2(16):='''';
     luhn_dig        number := 0;
     curr_dig        number:= 0;
     s_sum           number:= 0;
     outer_sum       number:= 0;
     check_num       number:= 0;
	 tmp_card_number varchar2(20):= trim(card_number);
     t_length        number:= length(tmp_card_number);
begin
     if t_length > 15 then
	   tmp_card_number := substr(tmp_card_number,1,12);
	   tmp_card_number := tmp_card_number || '000';
     end if;
     if t_length < 15 then
       for i in 1..(15-t_length)
	   loop
	     tmp_card_number := tmp_card_number || i ;
	     t_length        := length(tmp_card_number);
	   end loop;
	 else
	 	 tmp_card_number := tmp_card_number;
	 end if;
	 	 for i in 1..length(tmp_card_number)
     loop
           s_sum := 0;
           curr_dig := substr(tmp_card_number, i, 1);
           if mod(length(tmp_card_number),2)=1 then
                check_num := 1;
           else
                check_num := 0;
           end if;
           if mod(i, 2)=check_num then
                curr_dig := curr_dig*2;
                if curr_dig>9 then
                     for i in 1..length(curr_dig)
                     loop
                           s_sum := s_sum+substr(curr_dig,i,1);
                     end loop;
                else
                     s_sum := curr_dig;
                end if;
           else
                s_sum := curr_dig;
           end if;
           outer_sum := outer_sum + s_sum;
     end loop;
     luhn_dig := outer_sum*9;
     luhn_dig := substr(luhn_dig, length(luhn_dig), 1);
     new_card := tmp_card_number||luhn_dig;
     return new_card;
end;
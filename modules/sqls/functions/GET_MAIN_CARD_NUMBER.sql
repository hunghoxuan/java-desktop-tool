FUNCTION GET_MAIN_CARD_NUMBER
  (inst in varchar2, clnt in varchar2, grpno in varchar2, servno in varchar2)
RETURN  VARCHAR2
IS
  --
  cursor LatestActiveCard_cur --Get Oldest active card
  is
    select cards.card_number
    from   svc_client_cards cards, svc_card_information inf
    where  cards.institution_number = inst
           --  and cards.client_number = clnt
    and    cards.group_number = grpno
    and    cards.service_contract_id = servno
    and    inf.institution_number = cards.institution_number
    and    inf.card_number = cards.card_number
    and    inf.card_status = '001'
    and    inf.card_expiry_date || inf.effective_date || inf.time = (select max(card_expiry_date || effective_date || time)
                                          from svc_card_information
                                          where institution_number = inf.institution_number
                                          and card_number = inf.card_number)
    order  by cards.record_date asc;
    --
    cursor OldestNonactivecard_cur --Get latest non-active card if no active card was found.
    is
      select cards.card_number
      from   svc_client_cards cards, svc_card_information inf
      where  cards.institution_number = inst
             --  and cards.client_number = clnt
      and    cards.group_number = grpno
      and    cards.service_contract_id = servno
      and    inf.institution_number = cards.institution_number
      and    inf.card_number = cards.card_number
      and    inf.card_status <> '001'
      and    inf.card_expiry_date || inf.effective_date || inf.time = (select max(card_expiry_date || effective_date || time)
                                            from svc_card_information
                                            where institution_number = inf.institution_number
                                            and card_number = inf.card_number)
      order  by cards.record_date desc;
  --
  v_cardnum svc_client_cards.card_number%TYPE := NULL;
  --
BEGIN
  --
  OPEN  LatestActiveCard_cur;
  FETCH LatestActiveCard_cur INTO v_cardnum;
  --
  IF LatestActiveCard_cur%NOTFOUND THEN
    OPEN OldestNonactivecard_cur;
    FETCH OldestNonactivecard_cur INTO v_cardnum;
    CLOSE  OldestNonactivecard_cur;
  END IF;
  --
  CLOSE LatestActiveCard_cur;
  --
  RETURN   v_cardnum;
  --
EXCEPTION
WHEN OTHERS  THEN
  RETURN NULL;
END;

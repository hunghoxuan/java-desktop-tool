FUNCTION BWTPAD(
  strTableName IN  varchar2,
  strValue IN  varchar2
)
return varchar2
AS
  v_return varchar2(8);
  v_len    number := 0;
begin
  --
  if bwt_pad_cache.bwt_cache.exists(strTableName||'-'||strValue) then
    return bwt_pad_cache.bwt_cache(strTableName||'-'||strValue);
  else
    --
    case upper(strTableName)
    when 'BWT_CURRENCY' then
      v_len := getFieldLength(strTableName, 'ISO_CODE');
    else
      v_len := getFieldLength(strTableName, 'INDEX_FIELD');
    end case;
    --
    -- handling user-defined ranges - giving error when a user_defined range is used
    -- this has been removed from the bwtpad function for the following reasons:
    -- 1. BWT tables which are not extended do not fall within scope of this function
    -- 2. In the future when all the BWTs are extended this function become deprecated
    -- 3. Filtering of user defined constants required high maintenance in this function which has to be rolled out to clients everytime
    -- However we need to come up with a solution to discourage the declaration of constants in user-defined ranges.
    --
    case upper(strTableName)
    when 'BWT_TRANSACTION_TYPE' then
      --all digits except the last should be 9s
      --last digit should be either of 7, 8, 9
      --997 TRN_TYPES_ALL_CR, 998 TRN_TYPES_ALL_DR
      if substr(strValue, 1, length(strValue) - 1) in ('99', '999', '9999') and substr(strValue, -1) in ('7','8','9') then
        v_return := lpad(strValue, v_len,'9');
      else
        v_return := lpad(strValue, v_len,'0');
      end if;
      --
    else
      if strValue in ('999', '9999', '99999')  then
        v_return := lpad(strValue, v_len,'9');
      else
        v_return := lpad(strValue, v_len,'0');
      end if;
    end case;
    --
    bwt_pad_cache.bwt_cache(strTableName||'-'||strValue) := v_return;
    --
    return v_return;
  end if;
  --
end BWTPAD;
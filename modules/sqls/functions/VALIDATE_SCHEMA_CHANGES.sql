procedure validate_schema_changes (
procedure validate_schema_changes (
  p_script_name   in varchar2,
  p_script_name   in varchar2,
  p_table_name    in varchar2,
  p_table_name    in varchar2,
  p_pk_string     in varchar2,
  p_pk_string     in varchar2,
  p_unique_keys   in RISK_TAB_VARCHAR2_4000,
  p_unique_keys   in RISK_TAB_VARCHAR2_4000,
  p_foreign_keys  in RISK_TAB_VARCHAR2_4000,
  p_foreign_keys  in RISK_TAB_VARCHAR2_4000,
  p_columns_data  in RISK_TAB_VARCHAR2_4000
  p_columns_data  in RISK_TAB_VARCHAR2_4000
--  p_compare_table in out nocopy obj_schema_change
--  p_compare_table in out nocopy obj_schema_change
)
)
as
as
  --
  --
  C_COLUMN_NAME    CONSTANT VARCHAR2(12) := 'COLUMN_NAME';
  C_COLUMN_NAME    CONSTANT VARCHAR2(12) := 'COLUMN_NAME';
  C_COLUMN_ID      CONSTANT VARCHAR2(9)  := 'COLUMN_ID';
  C_COLUMN_ID      CONSTANT VARCHAR2(9)  := 'COLUMN_ID';
  C_DATA_TYPE      CONSTANT VARCHAR2(10) := 'DATA_TYPE';
  C_DATA_TYPE      CONSTANT VARCHAR2(10) := 'DATA_TYPE';
  C_DATA_LENGTH    CONSTANT VARCHAR2(12) := 'DATA_LENGTH';
  C_DATA_LENGTH    CONSTANT VARCHAR2(12) := 'DATA_LENGTH';
  C_CHAR_LENGTH    CONSTANT VARCHAR2(11) := 'CHAR_LENGTH';
  C_CHAR_LENGTH    CONSTANT VARCHAR2(11) := 'CHAR_LENGTH';
  C_NULLABLE       CONSTANT VARCHAR2(8)  := 'NULLABLE';
  C_NULLABLE       CONSTANT VARCHAR2(8)  := 'NULLABLE';
  C_DATA_DEFAULT   CONSTANT VARCHAR2(13) := 'DATA_DEFAULT';
  C_DATA_DEFAULT   CONSTANT VARCHAR2(13) := 'DATA_DEFAULT';
  C_COMMENT        CONSTANT VARCHAR2(7)  := 'COMMENT';
  C_COMMENT        CONSTANT VARCHAR2(7)  := 'COMMENT';
  C_DATA_PRECISION CONSTANT VARCHAR2(14) := 'DATA_PRECISION';
  C_DATA_PRECISION CONSTANT VARCHAR2(14) := 'DATA_PRECISION';
  C_DATA_SCALE     CONSTANT VARCHAR2(10) := 'DATA_SCALE';
  C_DATA_SCALE     CONSTANT VARCHAR2(10) := 'DATA_SCALE';
  --
  --
  v_index   varchar2(30);
  v_index   varchar2(30);
  i_counter pls_integer := 0;
  i_counter pls_integer := 0;
  --
  --
  --
  --
  varray_columns   varr_schema_change;
  varray_columns   varr_schema_change;
  p_compare_table  obj_schema_change;
  p_compare_table  obj_schema_change;
  --
  --
  err_no_data_passed exception;
  err_no_data_passed exception;
  --
  --
  --
  --
  --------------------------------------------------------
  --------------------------------------------------------
  --
  --
  function extract_value(
  function extract_value(
    init_string in varchar2,
    init_string in varchar2,
    col_name    in varchar2
    col_name    in varchar2
  )
  )
    return varchar2
    return varchar2
  as
  as
    --
    --
    i_len   integer := 0;
    i_len   integer := 0;
    i_len2  integer := 0;
    i_len2  integer := 0;
    --
    --
    v_start varchar2(4000);
    v_start varchar2(4000);
    v_ret   varchar2(4000);
    v_ret   varchar2(4000);
    --
    --
  begin
  begin
    --
    --
    i_len   := instr(init_string, col_name||':') + length(col_name)+1;
    i_len   := instr(init_string, col_name||':') + length(col_name)+1;
    v_start := substr( init_string, i_len );
    v_start := substr( init_string, i_len );
    i_len2  := instr(v_start, '|') - 1;
    i_len2  := instr(v_start, '|') - 1;
    v_ret   := trim(substr(init_string, i_len, i_len2));
    v_ret   := trim(substr(init_string, i_len, i_len2));
    --
    --
--    dbms_output.put_line('Returning: '||v_ret);
--    dbms_output.put_line('Returning: '||v_ret);
    --
    --
    return v_ret;
    return v_ret;
    --
    --
  end extract_value;
  end extract_value;
  --
  --
  --------------------------------------------------------
  --------------------------------------------------------
  --
  --
  --
  --
begin
begin
  --
  --
  -- Start by preparing the data from the input script.
  -- Start by preparing the data from the input script.
  varray_columns := varr_schema_change();
  varray_columns := varr_schema_change();
  --
  --
  if p_columns_data is null
  if p_columns_data is null
  or p_columns_data.COUNT < 1
  or p_columns_data.COUNT < 1
  then
  then
    raise err_no_data_passed;
    raise err_no_data_passed;
  end if;
  end if;
  --
  --
  for i in 1 .. p_columns_data.COUNT
  for i in 1 .. p_columns_data.COUNT
  loop
  loop
   --
   --
   -- schema_change_rec
   -- schema_change_rec
   varray_columns.EXTEND;
   varray_columns.EXTEND;
   varray_columns(varray_columns.COUNT) := schema_change_rec(
   varray_columns(varray_columns.COUNT) := schema_change_rec(
     extract_value(p_columns_data(i), C_COLUMN_NAME),
     extract_value(p_columns_data(i), C_COLUMN_NAME),
     extract_value(p_columns_data(i), C_COLUMN_ID),
     extract_value(p_columns_data(i), C_COLUMN_ID),
     extract_value(p_columns_data(i), C_DATA_TYPE),
     extract_value(p_columns_data(i), C_DATA_TYPE),
     extract_value(p_columns_data(i), C_DATA_LENGTH),
     extract_value(p_columns_data(i), C_DATA_LENGTH),
     extract_value(p_columns_data(i), C_CHAR_LENGTH),
     extract_value(p_columns_data(i), C_CHAR_LENGTH),
     extract_value(p_columns_data(i), C_DATA_PRECISION),
     extract_value(p_columns_data(i), C_DATA_PRECISION),
     extract_value(p_columns_data(i), C_DATA_SCALE),
     extract_value(p_columns_data(i), C_DATA_SCALE),
     extract_value(p_columns_data(i), C_NULLABLE),
     extract_value(p_columns_data(i), C_NULLABLE),
     extract_value(p_columns_data(i), C_DATA_DEFAULT),
     extract_value(p_columns_data(i), C_DATA_DEFAULT),
     extract_value(p_columns_data(i), C_COMMENT),
     extract_value(p_columns_data(i), C_COMMENT),
     0
     0
   );
   );
   --
   --
  end loop;
  end loop;
  --
  --
  p_compare_table := obj_schema_change(p_array=>varray_columns);
  p_compare_table := obj_schema_change(p_array=>varray_columns);
  --
  --
  --
  --
  --START JOB.
  --START JOB.
  BW_SCHEMA.begin_job_script(p_script_name);
  BW_SCHEMA.begin_job_script(p_script_name);
  --
  --
  -- CHANGES IN SCHEMA
  -- CHANGES IN SCHEMA
  for rec in (
  for rec in (
    select table_name, column_name, column_id, data_type, data_length, char_length, data_precision, data_scale, nullable
    select table_name, column_name, column_id, data_type, data_length, char_length, data_precision, data_scale, nullable
    from user_tab_columns
    from user_tab_columns
    where table_name = p_table_name
    where table_name = p_table_name
  )
  )
  loop
  loop
    --
    --
    i_counter := i_counter + 1; --increment counter.
    i_counter := i_counter + 1; --increment counter.
    --
    --
    --Loop through the column list and check if anything changed in the table's schema.
    --Loop through the column list and check if anything changed in the table's schema.
    declare
    declare
       d_data_type varchar2(50);
       d_data_type varchar2(50);
       v_nullable varchar2(1) := null;
       v_nullable varchar2(1) := null;
       b_changed_type boolean := False;
       b_changed_type boolean := False;
    begin
    begin
      --
      --
      -- Check DATA TYPE
      -- Check DATA TYPE
      if rec.DATA_TYPE <> p_compare_table.get_field(rec.column_name).DATA_TYPE then -- potential data loss ?????
      if rec.DATA_TYPE <> p_compare_table.get_field(rec.column_name).DATA_TYPE then -- potential data loss ?????
        dbms_output.put_line( utl_lms.format_message( 'Found change in DATA_TYPE for [%s.%s]. Was [%s] updating to [%s]', p_table_name, rec.column_name, rec.data_type, p_compare_table.get_field(rec.column_name).DATA_TYPE ) );
        dbms_output.put_line( utl_lms.format_message( 'Found change in DATA_TYPE for [%s.%s]. Was [%s] updating to [%s]', p_table_name, rec.column_name, rec.data_type, p_compare_table.get_field(rec.column_name).DATA_TYPE ) );
        d_data_type := p_compare_table.get_field(rec.column_name).DATA_TYPE;
        d_data_type := p_compare_table.get_field(rec.column_name).DATA_TYPE;
        b_changed_type := True;
        b_changed_type := True;
      end if;
      end if;
      --
      --
      -- Check DATA LENGTH
      -- Check DATA LENGTH
      if p_compare_table.get_field(rec.column_name).DATA_LENGTH is not null
      if p_compare_table.get_field(rec.column_name).DATA_LENGTH is not null
      and rec.data_length <> p_compare_table.get_field(rec.column_name).DATA_LENGTH then
      and rec.data_length <> p_compare_table.get_field(rec.column_name).DATA_LENGTH then
        if p_compare_table.get_field(rec.column_name).DATA_TYPE = 'VARCHAR2' then
        if p_compare_table.get_field(rec.column_name).DATA_TYPE = 'VARCHAR2' then
          dbms_output.put_line( utl_lms.format_message( 'Setting DATA_LENGTH for [%s.%s]. Was [%s] updating to [%s]', p_table_name, rec.column_name, ''||rec.data_length, ''||p_compare_table.get_field(rec.column_name).DATA_LENGTH ) );
          dbms_output.put_line( utl_lms.format_message( 'Setting DATA_LENGTH for [%s.%s]. Was [%s] updating to [%s]', p_table_name, rec.column_name, ''||rec.data_length, ''||p_compare_table.get_field(rec.column_name).DATA_LENGTH ) );
          if not b_changed_type then
          if not b_changed_type then
            d_data_type := 'VARCHAR2(' || p_compare_table.get_field(rec.column_name).DATA_LENGTH || ') ';
            d_data_type := 'VARCHAR2(' || p_compare_table.get_field(rec.column_name).DATA_LENGTH || ') ';
          else
          else
            d_data_type := d_data_type || '(' || p_compare_table.get_field(rec.column_name).DATA_LENGTH || ') ';
            d_data_type := d_data_type || '(' || p_compare_table.get_field(rec.column_name).DATA_LENGTH || ') ';
          end if;
          end if;
          b_changed_type := True;
          b_changed_type := True;
        end if;
        end if;
      end if;
      end if;
      --
      --
      -- Check DATA_PRECISION and DATA_SCALE for NUMBERs.
      -- Check DATA_PRECISION and DATA_SCALE for NUMBERs.
      if p_compare_table.get_field(rec.column_name).DATA_TYPE = 'NUMBER' then
      if p_compare_table.get_field(rec.column_name).DATA_TYPE = 'NUMBER' then
        --
        --
        -- DATA_PRECISION
        -- DATA_PRECISION
        if (p_compare_table.get_field(rec.column_name).DATA_PRECISION is not null
        if (p_compare_table.get_field(rec.column_name).DATA_PRECISION is not null
        and nvl(rec.data_precision, 0) <> p_compare_table.get_field(rec.column_name).DATA_PRECISION)
        and nvl(rec.data_precision, 0) <> p_compare_table.get_field(rec.column_name).DATA_PRECISION)
        -- NULL VALUES
        -- NULL VALUES
        or (p_compare_table.get_field(rec.column_name).DATA_PRECISION is null
        or (p_compare_table.get_field(rec.column_name).DATA_PRECISION is null
        and rec.data_precision is not null)
        and rec.data_precision is not null)
        then
        then
          --
          --
          if not b_changed_type then
          if not b_changed_type then
            if p_compare_table.get_field(rec.column_name).DATA_PRECISION is null then
            if p_compare_table.get_field(rec.column_name).DATA_PRECISION is null then
              d_data_type := 'NUMBER';
              d_data_type := 'NUMBER';
            else
            else
              d_data_type := 'NUMBER(' || p_compare_table.get_field(rec.column_name).DATA_PRECISION;
              d_data_type := 'NUMBER(' || p_compare_table.get_field(rec.column_name).DATA_PRECISION;
            end if;
            end if;
          else
          else
            if p_compare_table.get_field(rec.column_name).DATA_PRECISION is null then
            if p_compare_table.get_field(rec.column_name).DATA_PRECISION is null then
              d_data_type := d_data_type;
              d_data_type := d_data_type;
            else
            else
              d_data_type := d_data_type || '(' || p_compare_table.get_field(rec.column_name).DATA_PRECISION;
              d_data_type := d_data_type || '(' || p_compare_table.get_field(rec.column_name).DATA_PRECISION;
            end if;
            end if;
          end if;
          end if;
          --
          --
          -- DATA_SCALE
          -- DATA_SCALE
          if p_compare_table.get_field(rec.column_name).DATA_SCALE is not null
          if p_compare_table.get_field(rec.column_name).DATA_SCALE is not null
          and nvl(rec.data_precision, 0) <> p_compare_table.get_field(rec.column_name).DATA_SCALE
          and nvl(rec.data_precision, 0) <> p_compare_table.get_field(rec.column_name).DATA_SCALE
          then
          then
            --
            --
            if p_compare_table.get_field(rec.column_name).DATA_PRECISION is not null then
            if p_compare_table.get_field(rec.column_name).DATA_PRECISION is not null then
              d_data_type := d_data_type || ', ' || p_compare_table.get_field(rec.column_name).DATA_SCALE;
              d_data_type := d_data_type || ', ' || p_compare_table.get_field(rec.column_name).DATA_SCALE;
            end if;
            end if;
            --
            --
          end if;
          end if;
          -- add the final )
          -- add the final )
          if p_compare_table.get_field(rec.column_name).DATA_PRECISION is not null then
          if p_compare_table.get_field(rec.column_name).DATA_PRECISION is not null then
            d_data_type := d_data_type || ')';
            d_data_type := d_data_type || ')';
          end if;
          end if;
          --
          --
          b_changed_type := True;
          b_changed_type := True;
          --
          --
        end if;
        end if;
        --
        --
      end if; -- NUMBERs
      end if; -- NUMBERs
      --
      --
      -- Check NULLABLE
      -- Check NULLABLE
      if rec.nullable <> p_compare_table.get_field(rec.column_name).NULLABLE then
      if rec.nullable <> p_compare_table.get_field(rec.column_name).NULLABLE then
        DBMS_OUTPUT.PUT_LINE( utl_lms.format_message( 'Setting field [%s.%s] as NULLABLE: [%s]', p_table_name, rec.column_name, p_compare_table.get_field(rec.column_name).NULLABLE ) );
        DBMS_OUTPUT.PUT_LINE( utl_lms.format_message( 'Setting field [%s.%s] as NULLABLE: [%s]', p_table_name, rec.column_name, p_compare_table.get_field(rec.column_name).NULLABLE ) );
        --
        --
        if d_data_type is null then
        if d_data_type is null then
          if p_compare_table.get_field(rec.column_name).DATA_TYPE = 'VARCHAR2' then
          if p_compare_table.get_field(rec.column_name).DATA_TYPE = 'VARCHAR2' then
             d_data_type := 'VARCHAR2(' || p_compare_table.get_field(rec.column_name).DATA_LENGTH || ') ';
             d_data_type := 'VARCHAR2(' || p_compare_table.get_field(rec.column_name).DATA_LENGTH || ') ';
          else
          else
            d_data_type := p_compare_table.get_field(rec.column_name).DATA_TYPE;
            d_data_type := p_compare_table.get_field(rec.column_name).DATA_TYPE;
          end if;
          end if;
        end if;
        end if;
        --
        --
        -- Parameters in BW_SCHEMA.modify_table_column are swapped.
        -- Parameters in BW_SCHEMA.modify_table_column are swapped.
        if p_compare_table.get_field(rec.column_name).NULLABLE = 'N' then
        if p_compare_table.get_field(rec.column_name).NULLABLE = 'N' then
          v_nullable := 'Y';
          v_nullable := 'Y';
        else
        else
          v_nullable := 'N';
          v_nullable := 'N';
        end if;
        end if;
        b_changed_type := True;
        b_changed_type := True;
      end if;
      end if;
      --
      --
      if b_changed_type then
      if b_changed_type then
        BW_SCHEMA.modify_table_column(p_table_name, rec.column_name, d_data_type, p_nullable=>v_nullable);
        BW_SCHEMA.modify_table_column(p_table_name, rec.column_name, d_data_type, p_nullable=>v_nullable);
      end if;
      end if;
      --
      --
    exception
    exception
      when no_data_found then
      when no_data_found then
        dbms_output.put_line( utl_lms.format_message('Field [%s] was dropped from schema.', rec.column_name ) );
        dbms_output.put_line( utl_lms.format_message('Field [%s] was dropped from schema.', rec.column_name ) );
    end;
    end;
    --
    --
    p_compare_table.set_matched(rec.column_name);
    p_compare_table.set_matched(rec.column_name);
    --
    --
  end loop; -- Checks on individual fields.
  end loop; -- Checks on individual fields.
  --
  --
  -------------------------------------------------
  -------------------------------------------------
  -------------------------------------------------
  -------------------------------------------------
  --
  --
  -- CHANGES IN CONSTRAINTS
  -- CHANGES IN CONSTRAINTS
  -- declare
  -- declare
  --   v_pk_list varchar2(4000);
  --   v_pk_list varchar2(4000);
  --   v_pk_name varchar2(35);
  --   v_pk_name varchar2(35);
  --   v_pk_cols varchar2(4000);
  --   v_pk_cols varchar2(4000);
  -- begin
  -- begin
  --   --
  --   --
  --   v_pk_name := substr(p_pk_string, 1, instr(p_pk_string, '>')-1); --extract PK name from the pk string passed in.
  --   v_pk_name := substr(p_pk_string, 1, instr(p_pk_string, '>')-1); --extract PK name from the pk string passed in.
  --   v_pk_cols := replace(substr(p_pk_string, instr(p_pk_string, '>')+1), ' ', '');--remove spaces that the user might have passed.
  --   v_pk_cols := replace(substr(p_pk_string, instr(p_pk_string, '>')+1), ' ', '');--remove spaces that the user might have passed.
  --   --
  --   --
  --   select listagg(column_name, ',') within group(order by position)as cols
  --   select listagg(column_name, ',') within group(order by position)as cols
  --   into v_pk_list
  --   into v_pk_list
  --   from user_cons_columns
  --   from user_cons_columns
  --   where table_name = p_table_name
  --   where table_name = p_table_name
  --   and constraint_name = v_pk_name;
  --   and constraint_name = v_pk_name;
  --   --
  --   --
  --   if upper(v_pk_cols) <> upper(v_pk_list) then
  --   if upper(v_pk_cols) <> upper(v_pk_list) then
  --     /**
  --     /**
  --       Currently not directly supported by BW_SCHEMA.
  --       Currently not directly supported by BW_SCHEMA.
  --       Need to execute drop_constraint and do a new one.
  --       Need to execute drop_constraint and do a new one.
  --       This has to be reviewed before implemented.
  --       This has to be reviewed before implemented.
  --     */
  --     */
  --     --
  --     --
  --     null;
  --     null;
  --   end if;
  --   end if;
  --   --
  --   --
  -- exception
  -- exception
  --   when no_data_found then
  --   when no_data_found then
  --     --PK was not found. Try to create it.
  --     --PK was not found. Try to create it.
  --     BW_SCHEMA.create_table_pk_constraint(v_pk_name, p_table_name, v_pk_cols);
  --     BW_SCHEMA.create_table_pk_constraint(v_pk_name, p_table_name, v_pk_cols);
  -- end;
  -- end;
  --
  --
  -------------------------------------------------
  -------------------------------------------------
  -------------------------------------------------
  -------------------------------------------------
  --
  --
  --
  --
  if i_counter > 1 then -- NEW FIELDS
  if i_counter > 1 then -- NEW FIELDS
    --
    --
    --Check if all fields were caught in the loop above. If not it means it is a new fields and needs to be added to the table.
    --Check if all fields were caught in the loop above. If not it means it is a new fields and needs to be added to the table.
    v_index := p_compare_table.table_schema.FIRST;
    v_index := p_compare_table.table_schema.FIRST;
    while v_index is not null
    while v_index is not null
    loop
    loop
      --
      --
      declare
      declare
        d_type varchar2(50);
        d_type varchar2(50);
        v_nullable varchar2(1) := null;
        v_nullable varchar2(1) := null;
        v_default varchar2(500);
        v_default varchar2(500);
      begin
      begin
        --
        --
        if p_compare_table.table_schema(v_index).col_matched < 1 then
        if p_compare_table.table_schema(v_index).col_matched < 1 then
          dbms_output.put_line( utl_lms.format_message( 'Found new Field [%s]. Creating DDL to ALTER TABLE', p_compare_table.table_schema(v_index).column_name ) );
          dbms_output.put_line( utl_lms.format_message( 'Found new Field [%s]. Creating DDL to ALTER TABLE', p_compare_table.table_schema(v_index).column_name ) );
          --
          --
          if p_compare_table.table_schema(v_index).DATA_TYPE = 'VARCHAR2' then
          if p_compare_table.table_schema(v_index).DATA_TYPE = 'VARCHAR2' then
            d_type := 'VARCHAR2('||p_compare_table.table_schema(v_index).DATA_LENGTH||')';
            d_type := 'VARCHAR2('||p_compare_table.table_schema(v_index).DATA_LENGTH||')';
          elsif p_compare_table.table_schema(v_index).DATA_TYPE = 'NUMBER' then
          elsif p_compare_table.table_schema(v_index).DATA_TYPE = 'NUMBER' then
            d_type := 'NUMBER (';
            d_type := 'NUMBER (';
            if p_compare_table.table_schema(v_index).DATA_PRECISION is not null
            if p_compare_table.table_schema(v_index).DATA_PRECISION is not null
            or p_compare_table.table_schema(v_index).DATA_PRECISION > 0
            or p_compare_table.table_schema(v_index).DATA_PRECISION > 0
            then
            then
              d_type := d_type||p_compare_table.table_schema(v_index).DATA_PRECISION;
              d_type := d_type||p_compare_table.table_schema(v_index).DATA_PRECISION;
              --
              --
              -- SCALE needs PRECISION
              -- SCALE needs PRECISION
              if p_compare_table.table_schema(v_index).DATA_SCALE is not null then
              if p_compare_table.table_schema(v_index).DATA_SCALE is not null then
                d_type := d_type||', '||p_compare_table.table_schema(v_index).DATA_SCALE;
                d_type := d_type||', '||p_compare_table.table_schema(v_index).DATA_SCALE;
              end if;
              end if;
              --
              --
            end if;
            end if;
            d_type := d_type||')';
            d_type := d_type||')';
          else
          else
            d_type := p_compare_table.table_schema(v_index).DATA_TYPE;
            d_type := p_compare_table.table_schema(v_index).DATA_TYPE;
          end if;
          end if;
          --
          --
          if p_compare_table.table_schema(v_index).DATA_DEFAULT is not null then
          if p_compare_table.table_schema(v_index).DATA_DEFAULT is not null then
            v_default := p_compare_table.table_schema(v_index).DATA_DEFAULT;
            v_default := p_compare_table.table_schema(v_index).DATA_DEFAULT;
          end if;
          end if;
          --
          --
          -- Parameters in BW_SCHEMA.modify_table_column are swapped.
          -- Parameters in BW_SCHEMA.modify_table_column are swapped.
          if p_compare_table.table_schema(v_index).nullable = 'N' then
          if p_compare_table.table_schema(v_index).nullable = 'N' then
            v_nullable := 'Y';
            v_nullable := 'Y';
          else
          else
            v_nullable := 'N';
            v_nullable := 'N';
          end if;
          end if;
          --
          --
          BW_SCHEMA.add_table_column(p_table_name, p_compare_table.table_schema(v_index).column_name, d_type, p_col_default => v_default, p_nullable => v_nullable);
          BW_SCHEMA.add_table_column(p_table_name, p_compare_table.table_schema(v_index).column_name, d_type, p_col_default => v_default, p_nullable => v_nullable);
          --
          --
        end if;
        end if;
      end;
      end;
      --
      --
      v_index := p_compare_table.table_schema.NEXT(v_index);
      v_index := p_compare_table.table_schema.NEXT(v_index);
      --
      --
    end loop;
    end loop;
    --
    --
  else -- NEW TABLE
  else -- NEW TABLE
    --
    --
    -- Create new Table as it does not exist.
    -- Create new Table as it does not exist.
    dbms_output.put_line( utl_lms.format_message( 'New table! Generating DDL and creating table [%s]', p_table_name ) );
    dbms_output.put_line( utl_lms.format_message( 'New table! Generating DDL and creating table [%s]', p_table_name ) );
    declare
    declare
      --
      --
      v_columns         varchar2(4000);
      v_columns         varchar2(4000);
      v_ind             varchar2(30);
      v_ind             varchar2(30);
      v_related_table   varchar2(50);
      v_related_table   varchar2(50);
      v_related_columns varchar2(4000);
      v_related_columns varchar2(4000);
      v_table_columns   varchar2(4000);
      v_table_columns   varchar2(4000);
      v_current_key     varchar2(4000);
      v_current_key     varchar2(4000);
      v_key_name        varchar2(4000);
      v_key_name        varchar2(4000);
      v_options         varchar2(4000) := null;
      v_options         varchar2(4000) := null;
      --
      --
    begin
    begin
      --
      --
      -- TABLE FIELDS
      -- TABLE FIELDS
      v_ind := p_compare_table.table_schema.FIRST;
      v_ind := p_compare_table.table_schema.FIRST;
      while v_ind is not null
      while v_ind is not null
      loop
      loop
        --
        --
        v_columns := v_columns||p_compare_table.table_schema(v_ind).column_name||' '||p_compare_table.table_schema(v_ind).DATA_TYPE;
        v_columns := v_columns||p_compare_table.table_schema(v_ind).column_name||' '||p_compare_table.table_schema(v_ind).DATA_TYPE;
        --
        --
        if p_compare_table.table_schema(v_ind).DATA_TYPE = 'VARCHAR2' then
        if p_compare_table.table_schema(v_ind).DATA_TYPE = 'VARCHAR2' then
          v_columns := v_columns || '('||p_compare_table.table_schema(v_ind).DATA_LENGTH||') ';
          v_columns := v_columns || '('||p_compare_table.table_schema(v_ind).DATA_LENGTH||') ';
        elsif p_compare_table.table_schema(v_ind).DATA_TYPE = 'NUMBER' then
        elsif p_compare_table.table_schema(v_ind).DATA_TYPE = 'NUMBER' then
          if p_compare_table.table_schema(v_ind).DATA_PRECISION is not null
          if p_compare_table.table_schema(v_ind).DATA_PRECISION is not null
          or p_compare_table.table_schema(v_ind).DATA_PRECISION > 0
          or p_compare_table.table_schema(v_ind).DATA_PRECISION > 0
          then
          then
            v_columns := v_columns || '('||p_compare_table.table_schema(v_ind).DATA_PRECISION;
            v_columns := v_columns || '('||p_compare_table.table_schema(v_ind).DATA_PRECISION;
            --
            --
            if p_compare_table.table_schema(v_ind).DATA_SCALE is not null then
            if p_compare_table.table_schema(v_ind).DATA_SCALE is not null then
              v_columns := v_columns || ', ' || p_compare_table.table_schema(v_ind).DATA_SCALE;
              v_columns := v_columns || ', ' || p_compare_table.table_schema(v_ind).DATA_SCALE;
            end if;
            end if;
            --
            --
            v_columns := v_columns||')';
            v_columns := v_columns||')';
            --
            --
          end if;
          end if;


          -- v_columns := v_columns || '('||p_compare_table.table_schema(v_ind).DATA_PRECISION||', '||p_compare_table.table_schema(v_ind).DATA_SCALE||') ';
          -- v_columns := v_columns || '('||p_compare_table.table_schema(v_ind).DATA_PRECISION||', '||p_compare_table.table_schema(v_ind).DATA_SCALE||') ';
        end if;
        end if;
        --
        --
        if p_compare_table.table_schema(v_ind).DATA_DEFAULT is not null then
        if p_compare_table.table_schema(v_ind).DATA_DEFAULT is not null then
          v_columns := v_columns || ' DEFAULT '''||p_compare_table.table_schema(v_ind).DATA_DEFAULT||''' ';
          v_columns := v_columns || ' DEFAULT '''||p_compare_table.table_schema(v_ind).DATA_DEFAULT||''' ';
        end if;
        end if;
        --
        --
        if p_compare_table.table_schema(v_ind).NULLABLE = 'N' then
        if p_compare_table.table_schema(v_ind).NULLABLE = 'N' then
          v_columns := v_columns || ' NOT NULL ';
          v_columns := v_columns || ' NOT NULL ';
        end if;
        end if;
        --
        --
        v_columns := v_columns || ', ';
        v_columns := v_columns || ', ';
        --
        --
        v_ind := p_compare_table.table_schema.NEXT(v_ind);
        v_ind := p_compare_table.table_schema.NEXT(v_ind);
        --
        --
      end loop;
      end loop;
      --
      --
      -- remove last ','
      -- remove last ','
      v_columns := substr(v_columns, 1, instr(v_columns, ',', -1) - 1);
      v_columns := substr(v_columns, 1, instr(v_columns, ',', -1) - 1);
      --
      --
      -- dbms_output.put_line( 'Columns: '||v_columns );
      -- dbms_output.put_line( 'Columns: '||v_columns );
      BW_SCHEMA.create_table(p_table_name, v_columns);
      BW_SCHEMA.create_table(p_table_name, v_columns);
      --
      --
      --PRIMARY KEY
      --PRIMARY KEY
      v_key_name      := substr(p_pk_string, 1, instr(p_pk_string, '>')-1); --extract PK name from the pk string passed in.
      v_key_name      := substr(p_pk_string, 1, instr(p_pk_string, '>')-1); --extract PK name from the pk string passed in.
      v_table_columns := substr(p_pk_string, instr(p_pk_string, '>')+1);
      v_table_columns := substr(p_pk_string, instr(p_pk_string, '>')+1);
      BW_SCHEMA.create_table_pk_constraint(v_key_name, p_table_name, v_table_columns);
      BW_SCHEMA.create_table_pk_constraint(v_key_name, p_table_name, v_table_columns);
      --
      --
      --reset just in case.
      --reset just in case.
      v_key_name := null;
      v_key_name := null;
      v_table_columns := null;
      v_table_columns := null;
      --
      --
      -- Check if there are any UNIQUE KEYS to be added to the table.
      -- Check if there are any UNIQUE KEYS to be added to the table.
      if p_unique_keys is not null
      if p_unique_keys is not null
      and p_unique_keys.COUNT > 0
      and p_unique_keys.COUNT > 0
      then
      then
        --
        --
        for a in 1 .. p_unique_keys.COUNT
        for a in 1 .. p_unique_keys.COUNT
        loop
        loop
          --
          --
          v_current_key   := p_unique_keys(a);
          v_current_key   := p_unique_keys(a);
          v_key_name      := substr(v_current_key, 1, instr(v_current_key, '>')-1);
          v_key_name      := substr(v_current_key, 1, instr(v_current_key, '>')-1);
          v_table_columns := substr(v_current_key, instr(v_current_key, '>')+1);
          v_table_columns := substr(v_current_key, instr(v_current_key, '>')+1);
          --
          --
          BW_SCHEMA.create_table_unique_constraint(v_key_name, p_table_name, v_table_columns);
          BW_SCHEMA.create_table_unique_constraint(v_key_name, p_table_name, v_table_columns);
          --
          --
        end loop;
        end loop;
        --
        --
      end if;
      end if;
      --
      --
      -- reset values just in case.
      -- reset values just in case.
      v_current_key := null;
      v_current_key := null;
      v_key_name := null;
      v_key_name := null;
      v_table_columns := null;
      v_table_columns := null;
      --
      --
      -- FOREIGN KEYS
      -- FOREIGN KEYS
      if p_foreign_keys is not null
      if p_foreign_keys is not null
      and p_foreign_keys.COUNT > 0
      and p_foreign_keys.COUNT > 0
      then
      then
        --
        --
        for a in 1 .. p_foreign_keys.COUNT
        for a in 1 .. p_foreign_keys.COUNT
        loop
        loop
          --
          --
          --Format should be: RELATED_TABLE_NAME:COLUMN_1,COLUMN_2|RELATED_1,RELATED_2
          --Format should be: RELATED_TABLE_NAME:COLUMN_1,COLUMN_2|RELATED_1,RELATED_2
          v_current_key     := p_foreign_keys(a);
          v_current_key     := p_foreign_keys(a);
          v_key_name        := substr(v_current_key, 1, instr(v_current_key, '>')-1);
          v_key_name        := substr(v_current_key, 1, instr(v_current_key, '>')-1);
          v_related_table   := substr(v_current_key, instr(v_current_key, '>')+1, (instr(v_current_key, ':')) - (instr(v_current_key, '>')+1) );
          v_related_table   := substr(v_current_key, instr(v_current_key, '>')+1, (instr(v_current_key, ':')) - (instr(v_current_key, '>')+1) );
          v_table_columns   := substr(v_current_key, instr(v_current_key, ':')+1, (instr(v_current_key, '|')-1) - instr(v_current_key, ':') );
          v_table_columns   := substr(v_current_key, instr(v_current_key, ':')+1, (instr(v_current_key, '|')-1) - instr(v_current_key, ':') );
          v_related_columns := substr(v_current_key, instr(v_current_key, '|')+1);
          v_related_columns := substr(v_current_key, instr(v_current_key, '|')+1);
          --
          --
          if (p_table_name like '%RISK%'
          if (p_table_name like '%RISK%'
                  and p_table_name not in ('SYS_RISK_DATA_LOG', 'SYS_RISK_DATA_PARAMETER', 'SYS_RISK_MESSAGE_LOG')
                  and p_table_name not in ('SYS_RISK_DATA_LOG', 'SYS_RISK_DATA_PARAMETER', 'SYS_RISK_MESSAGE_LOG')
                  and p_table_name not like 'SIM%') then
                  and p_table_name not like 'SIM%') then
            v_options := 'DEFERRABLE INITIALLY IMMEDIATE';
            v_options := 'DEFERRABLE INITIALLY IMMEDIATE';
          end if;
          end if;




          --
          --
          v_current_key := ''; --reset just in case.
          v_current_key := ''; --reset just in case.
          --
          --
          BW_SCHEMA.create_table_fk_constraint(v_key_name, p_table_name, v_related_table, v_table_columns, v_related_columns, v_options);
          BW_SCHEMA.create_table_fk_constraint(v_key_name, p_table_name, v_related_table, v_table_columns, v_related_columns, v_options);
          --
          --
        end loop;
        end loop;
        --
        --
      end if;
      end if;
      --
      --
    end;
    end;
    --
    --
  end if;
  end if;
  --
  --
  dbms_output.put_line( 'DDL script finished sucessfully' );
  dbms_output.put_line( 'DDL script finished sucessfully' );
  --
  --
  BW_SCHEMA.end_job_script;
  BW_SCHEMA.end_job_script;
  --
  --
end;
end;

/*
Copyright (c) 2015 James Ahlborn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package twx.core.db.sqlbuilder.custom.postgresql;

import java.io.IOException;

import twx.core.db.common.util.AppendableExt;
import twx.core.db.sqlbuilder.Converter;
import twx.core.db.sqlbuilder.SelectQuery;
import twx.core.db.sqlbuilder.SqlObject;
import twx.core.db.sqlbuilder.ValidationContext;
import twx.core.db.sqlbuilder.ValidationException;
import twx.core.db.sqlbuilder.Verifiable;
import twx.core.db.sqlbuilder.custom.CustomSyntax;
import twx.core.db.sqlbuilder.custom.HookType;


/**
 * Appends a PostgreSQL offset clause like {@code " OFFSET <offset>"} for use in
 * {@link SelectQuery}s.
 *
 * @see SelectQuery#addCustomization(CustomSyntax)
 *
 * @author James Ahlborn
 */
public class PgOffsetClause extends CustomSyntax 
  implements Verifiable<PgOffsetClause>
{
  private SqlObject _value;

  public PgOffsetClause(Object value) {
    _value = Converter.toValueSqlObject(value);
  }

  @Override
  public void apply(SelectQuery query) {
    query.addCustomization(SelectQuery.Hook.FOR_UPDATE, HookType.BEFORE, this);
  }

  @Override
  public void appendTo(AppendableExt app) throws IOException
  {
    app.append(" OFFSET ");
    app.append(_value);
  }

  @Override
  protected void collectSchemaObjects(ValidationContext vContext) {
    vContext.addVerifiable(this);
    collectSchemaObjects(_value, vContext);
  }

  @Override
  public final PgOffsetClause validate() throws ValidationException {
    doValidate();
    return this;
  }

  @Override
  public void validate(ValidationContext vContext)
    throws ValidationException
  {
    if(_value == null) {
      throw new ValidationException("Offset clause is missing row count");
    }
    PgLimitClause.validateValue(_value, "Offset");
  }
}

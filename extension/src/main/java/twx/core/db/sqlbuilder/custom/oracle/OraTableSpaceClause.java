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

package twx.core.db.sqlbuilder.custom.oracle;

import java.io.IOException;

import twx.core.db.common.util.AppendableExt;
import twx.core.db.sqlbuilder.CreateIndexQuery;
import twx.core.db.sqlbuilder.CreateTableQuery;
import twx.core.db.sqlbuilder.ValidationContext;
import twx.core.db.sqlbuilder.custom.CustomSyntax;
import twx.core.db.sqlbuilder.custom.HookType;

/**
 * Appends an Oracle {@code " TABLESPACE ..."} clause to a {@link
 * CreateTableQuery} or {@link CreateIndexQuery} if a tableSpace has been
 * specified.
 * 
 * @see CreateTableQuery#addCustomization(CustomSyntax)
 * @see CreateIndexQuery#addCustomization(CustomSyntax)
 *
 * @author James Ahlborn
 */
public class OraTableSpaceClause extends CustomSyntax
{
  private String _tableSpace;

  public OraTableSpaceClause(String tableSpace) {
    _tableSpace = tableSpace;
  }

  @Override
  public void apply(CreateTableQuery query) {
    query.addCustomization(CreateTableQuery.Hook.TRAILER, HookType.BEFORE, this);
  }

  @Override
  public void apply(CreateIndexQuery query) {
    query.addCustomization(CreateIndexQuery.Hook.TRAILER, HookType.BEFORE, this);
  }

  @Override
  public void appendTo(AppendableExt app) throws IOException {
    if (_tableSpace != null) {
      app.append(" TABLESPACE " + _tableSpace);
    }
  }

  @Override
  protected void collectSchemaObjects(ValidationContext vContext) {
    // nothing to do
  }
}

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

import twx.core.db.sqlbuilder.ValidationContext;
import java.io.IOException;
import twx.core.db.common.util.AppendableExt;
import twx.core.db.sqlbuilder.custom.HookType;
import twx.core.db.sqlbuilder.CreateTableQuery;
import twx.core.db.sqlbuilder.custom.CustomSyntax;

/**
 * Miscellaneous useful constructs for custom PostgreSQL syntax.
 *
 * @author James Ahlborn
 */
public class PgObjects 
{
  /**
   * Appends a PostgreSQL {@code "IF NOT EXISTS "} modifier after the
   * {@code "CREATE TABLE "} query clause.
   */
  public static final CustomSyntax IF_NOT_EXISTS_TABLE = new CustomSyntax()
  {
    @Override
    public void apply(CreateTableQuery query) {
      query.addCustomization(CreateTableQuery.Hook.TABLE, HookType.SUFFIX, this);
    }

    @Override
    public void appendTo(AppendableExt app) throws IOException {
      app.append("IF NOT EXISTS ");
    }

    @Override
    protected void collectSchemaObjects(ValidationContext vContext) {}
  };


  private PgObjects() {}

}

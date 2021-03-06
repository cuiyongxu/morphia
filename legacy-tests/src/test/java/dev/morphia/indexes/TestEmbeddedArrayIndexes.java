/*
  Copyright (C) 2010 Olafur Gauti Gudmundsson
  <p/>
  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
  obtain a copy of the License at
  <p/>
  http://www.apache.org/licenses/LICENSE-2.0
  <p/>
  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
  and limitations under the License.
 */

package dev.morphia.indexes;

import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Indexes;
import dev.morphia.mapping.codec.pojo.EntityModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Scott Hernandez
 */
public class TestEmbeddedArrayIndexes extends TestBase {
    @Test
    public void testParamEntity() {
        final EntityModel entityModel = getMapper().getEntityModel(A.class);
        assertNotNull(entityModel);

        assertNotNull(entityModel.getAnnotation(Indexes.class));

        getDs().ensureIndexes(A.class);

        List<Document> indexInfo = getIndexInfo(A.class);
        assertEquals("indexes found: coll.getIndexInfo()" + indexInfo, 3, indexInfo.size());

    }

    @Entity
    @Indexes({@Index(fields = {@Field("b.bar"), @Field("b.car")})})
    private static class A {
        @Id
        private final ObjectId id = new ObjectId();
        private Set<B> b;
        @Indexed
        private String foo;
    }

    @Embedded
    private static class B {
        private String bar;
        private String car;
    }

}

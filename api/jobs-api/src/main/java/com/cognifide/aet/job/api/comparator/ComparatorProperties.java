/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.api.comparator;

import com.google.common.base.Objects;

import com.cognifide.aet.job.api.StepProperties;

public class ComparatorProperties extends StepProperties {

  private static final long serialVersionUID = -6266485111451748119L;

  private final String collectedId;

  public ComparatorProperties(String company, String project, String patternId, String collectedId) {
    super(company, project, patternId);
    this.collectedId = collectedId;
  }

  public String getCollectedId() {
    return collectedId;
  }


  @Override
  public String toString() {
    return Objects.toStringHelper(this)
            .add("collectedId", collectedId)
            .add("company", getCompany())
            .add("project", getProject())
            .add("patternId", getPatternId())
            .toString();
  }
}

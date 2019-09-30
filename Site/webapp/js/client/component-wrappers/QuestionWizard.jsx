import React from 'react';
import { connect } from 'react-redux';
import { IconAlt, Link } from 'wdk-client/Components';
import { getStudyByQuestionName } from '../selectors/siteData';

const injectSearchStudy = connect((state, props) => ({
  activeStudy: getStudyByQuestionName(props.wizardState.question.name)(state)
}));


export default QuestionWizard => injectSearchStudy(props => {
  let { activeStudy } = props;
  return (
    <div>
      {activeStudy == null
        ? "Could not find study based on the record class."
        : (
          <div className="clinepi-StudyLink">
            <Link to={activeStudy.route} _target="blank" >Study Details >></Link> 
          </div>
        )
      }
      <QuestionWizard {...props} />
    </div>
  )
})

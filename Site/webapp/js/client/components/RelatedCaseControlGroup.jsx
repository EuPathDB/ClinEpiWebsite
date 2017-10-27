import React from 'react';
import Param from 'ebrc-client/components/Param';

const CASE_CONTROL_GROUP_NAME = 'relative_case_control';
const TOGGLE_PARAM_NAME = 'use_rel_case_control';
const KEEP_REMOVE_PARAM_NAME = 'keep_remove';
const FILTER_PARAM_NAME = 'relative_event_gems';

export default class RelatedCaseControlGroup extends React.Component {

  static shouldUseLayout(props) {
    return props.wizardState.activeGroup.name === CASE_CONTROL_GROUP_NAME;
  }

  isEnabled() {
    return this.props.wizardState.paramValues[TOGGLE_PARAM_NAME] === 'Yes';
  }

  renderToggle() {
    return (
      <div className="CaseControlMessage">
        <label>
          <input
            type="checkbox"
            checked={this.isEnabled()}
            onClick={() => {
              this.props.eventHandlers.setParamValue(
                this.props.wizardState.question.parameters.find(p => p.name === TOGGLE_PARAM_NAME),
                this.isEnabled() ? 'No' : 'Yes'
              );
            }} /> Enable the advanced <strong>Related Case/Control</strong> filter below. It allows you to restrict Participants by relating them to your choice of case or control Participants.
        </label>
      </div>
    )
  }

  renderOverlay() {
    return this.isEnabled() ? null : (
      <div className="CaseControlLayoutOverlay"/>
    );
  }

  renderParam(param) {
    return (
      <Param
        param={param}
        value={this.props.wizardState.paramValues[param.name]}
        uiState={this.props.wizardState.paramUIState[param.name]}
        onActiveOntologyTermChange={this.props.eventHandlers.onActiveOntologyTermChange}
        onParamValueChange={this.props.eventHandlers.onParamValueChange}
        onParamStateChange={this.props.eventHandlers.onParamStateChange}
      />
    );
  }

  render() {
    const modifiedWizardState = Object.assign({}, this.props.wizardState, {
      activeGroup: Object.assign({}, this.props.wizardState.activeGroup, {
        parameters: []
      })
    });

    const paramMap = new Map(this.props.wizardState.question.parameters.map(p => [p.name, p]));
    const keepRemoveParam = paramMap.get(KEEP_REMOVE_PARAM_NAME);
    const filterParam = paramMap.get(FILTER_PARAM_NAME);

    return (
      <div className={'CaseControlGroupWrapper CaseControlGroupWrapper__' +
          (this.isEnabled() ? 'on' : 'off')}>
        <this.props.DefaultComponent {...this.props} wizardState={modifiedWizardState}/>
        {this.renderToggle()}
        <div className="CaseControlContainer">
          <div style={{padding: '1em 0'}}>
            {this.renderParam(keepRemoveParam)} <strong>Participants</strong> based on your choice of <strong>Related Case/Control</strong> Participants below
          </div>
          {this.renderParam(filterParam)}
          {this.renderOverlay()}
        </div>
      </div>
    )
  }
}

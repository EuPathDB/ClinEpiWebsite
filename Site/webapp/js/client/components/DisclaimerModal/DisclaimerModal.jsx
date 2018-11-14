import React from 'react';

import './DisclaimerModal.scss';
import Modal from 'ebrc-client/App/Modal';
import { IconAlt as Icon } from 'wdk-client/Components'

const logoTextStyle = {
  fontFamily: '"Exo 2"',
  fontWeight: 600,
  letterSpacing: '-2px'
};

const Text = {
  product: (
    <span>
      <big>ClinEpi<span style={{ color: '#DD314E' }}>DB</span> </big>
    </span>
  ),
  body: (
    <div>
      <p>Clinical Epidemiology Database Resources, ClinEpiDB (http://ClinEpiDB.org) is charged with ensuring that epidemiologic studies are conveniently accessible to the worldwide community of biomedical researchers. ClinEpiDB is committed to high standards for the protection of all submitted study data provided to ClinEpiDB and made available through the ClinEpiDB platform.</p>
      <p>Study data is interpretable only to the extent as the study design and context are understood. All dates are obfuscated per participant through the application of a random number algorithm to comply with the ethical conduct of human subjects research. Each study that is submitted to ClinEpiDB is unique and each may require different levels of data access restrictions. Researchers accessing data on ClinEpiDB agree to these policies and to comply with responsible authorized use. <a href="/documents/DataAccessUsePolicyClinEpiDB.latest.pdf" target="_blank">Read more about our data access and use policy here</a>. Use the ‘Contact Us’ link above to submit questions or comments.</p>
    </div>
  ),
  action: <span>Click here to acknowledge your agreement. <Icon fa="chevron-right right-side" /></span>
};

const flag = 'acceptedClinEpiDisclaimer=true';

class DisclaimerModal extends React.Component {
  constructor (props) {
    super(props);
    this.state = {
      showModal: document.cookie.indexOf(flag) < 0
    };

    this.acceptDisclaimer = this.acceptDisclaimer.bind(this);
  }

  acceptDisclaimer () {
    document.cookie = flag;
    const showModal = false;
    this.setState({ showModal });
  }

  render () {
    const { showModal } = this.state;
    const { action, body, headline, product } = Text;
    return (
      <Modal when={showModal} className="DisclaimerModal">
        {!product ? null : <h1 className="DisclaimerModal-Product">{product}</h1>}
        {!headline ? null : <h2 className="DisclaimerModal-Headline">{headline}</h2>}
        {!body ? null : <section className="DisclaimerModal-Body">{body}</section>}
        {!action ? null : <button className="DisclaimerModal-Button btn" onClick={this.acceptDisclaimer}>{action}</button>}
      </Modal>
    )
  }
};


export default DisclaimerModal;
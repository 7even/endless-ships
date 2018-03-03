import React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Panel } from 'react-bootstrap';
import { kebabCase } from '../common';

const OutfitPage = ({ outfit }) => (
  <div className="app">
    <Row>
      <Col md={12}>
        <Panel>
          <Panel.Heading>{outfit.name}</Panel.Heading>
        </Panel>
      </Col>
    </Row>
  </div>
);

const mapStateToProps = (state, { match: { params: { outfitName } } }) => {
  return {
    outfit: state.outfits.find(outfit => kebabCase(outfit.name) === outfitName)
  };
};

export default connect(mapStateToProps)(OutfitPage);

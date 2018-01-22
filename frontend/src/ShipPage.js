import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Grid, Row, Col, Panel, Image } from 'react-bootstrap';
import NumberFormat from 'react-number-format';
import './ShipPage.css';

function FormattedNumber(props) {
  return (
    <NumberFormat value={props.number}
                  displayType={'text'}
                  thousandSeparator={true} />
  );
}

class ShipPage extends Component {
  formatDescription() {
    return this.props.ship.description.map((line, index) => (
      <p key={`${this.props.ship.name}-description-${index}`}>{line}</p>
    ));
  }

  render() {
    console.log(this.props.ship);
    return (
      <div className="app">
        <ol className="breadcrumb">
          <li><Link to="/">Ships</Link></li>
          <li className="active">{this.props.ship.name}</li>
        </ol>

        <Grid>
          <Row>
            <Col md={6}>
              <Panel>
                <Panel.Heading>{this.props.ship.name}</Panel.Heading>

                <Panel.Body>
                  <div className="media">
                    <div className="media-body">
                      <ul>
                        <li>cost: <FormattedNumber number={this.props.ship.cost} /></li>
                        <li>shields: <FormattedNumber number={this.props.ship.shields} /></li>
                        <li>hull: <FormattedNumber number={this.props.ship.hull} /></li>
                        <li>mass: <FormattedNumber number={this.props.ship.mass} /></li>
                        <li>cargo space: <FormattedNumber number={this.props.ship.cargoSpace} /></li>
                        <li>required crew: <FormattedNumber number={this.props.ship.requiredCrew} /></li>
                        <li>bunks: <FormattedNumber number={this.props.ship.bunks} /></li>
                        <li>fuel capacity: <FormattedNumber number={this.props.ship.fuelCapacity} /></li>
                        <li>outfit space: <FormattedNumber number={this.props.ship.outfitSpace} /></li>
                        <li>weapon capacity: <FormattedNumber number={this.props.ship.weaponCapacity} /></li>
                        <li>engine capacity: <FormattedNumber number={this.props.ship.engineCapacity} /></li>
                      </ul>
                    </div>

                    <div className="media-right">
                      <Image src="https://raw.githubusercontent.com/endless-sky/endless-sky/master/images/ship/bactrian.png?raw=true" className="ship-sprite" />
                    </div>
                  </div>
                </Panel.Body>
              </Panel>
            </Col>
          </Row>

          <Row>
            <Col md={12}>
              <Panel>
                <Panel.Body>{this.formatDescription()}</Panel.Body>
              </Panel>
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}

export default ShipPage;

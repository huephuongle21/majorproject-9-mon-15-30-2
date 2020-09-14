import React, { Component } from 'react';
import CreateBooking from '../../actions/HandleBookings';
import Workers from '../../actions/HandleWorkers';
import Services from '../../actions/HandleServices';
import CustomerDashboard from '../Customer/CustomerDashBoard';
import Booking from '../../actions/HandleBookings';



class NewBookings extends Component {

    constructor(){
        super();

        this.state={
            allworker: [],
            allservices: [],
            availableSessions:[],
            customer: {
                id: "",
                fName: "",
                lName: "",
                address: "",
                phoneNumber: "",
                email: "",
                hibernateLazyInitializer: {}
            },
            worker: {
                id: "",
                fName: "",
                lName: "",
                admin: {
                    id: "",
                    adminName: "",
                    service: "",
                    hibernateLazyInitializer: {}
                },
                hibernateLazyInitializer: {}
            },
            status: "",
            date: "",
            startTime: "",
            endTime: "",
            service: ""
        };
        this.onChange = this.onChange.bind(this);
        this.onSubmit = this.onSubmit.bind(this);
        this.handleServiceChange = this.handleServiceChange.bind(this);
        this.handleWorkerSelection = this.handleWorkerSelection.bind(this);
    }


    handleServiceChange(e)
    {
        this.setState({[e.target.name]: e.target.value});
        const servicevalue = e.target.value;

        console.log("Service value selected: " + servicevalue);

        Workers.getWorkerByService(servicevalue).then((res) => {
            if(!res.data.empty)
            {
                console.log(res.data);
                this.setState({ allworker: res.data});
            }
            else{
                console.log("Empty");
            }
            
        });
    }

    handleWorkerSelection(e)
    {
        this.setState({[e.target.name]: e.target.value});

        const worker_id = e.target.value;
        const servicevalue = this.state.service;
        console.log("Selected worker id: " + worker_id);
        console.log("Selected service: " + servicevalue);

        Booking.getAvailableSessionsByWorkerAndService(worker_id, servicevalue).then((res) => {
            if(!res.data.empty)
            {
                console.log(res.data);
                this.setState({ availableSessions: res.data});
            }
            else
            {
                console.log("Empty");
            }
        })
    }

    onChange(e)
    {
        this.setState({[e.target.name]: e.target.value});
    }

    onSubmit(e){
        e.preventDefault();
        
        const newbookings = {

            customer: {
                id: "3",
                fName: "customer",
                lName: "one",
                address: "Phnom Penh",
                phoneNumber: 1234567,
                email: "customer1@gmail.com",
                hibernateLazyInitializer: {}
            },
            worker: {
                id: this.state.worker,
                fName: "",
                lName: "",
                admin: {
                    id: "",
                    adminName: "",
                    service: "",
                    hibernateLazyInitializer: {}
                },
                hibernateLazyInitializer: {}
            },
            status: "NEW_BOOKING",
            date: this.state.start_date,
            startTime: this.state.start_time + ":00",
            endTime: this.state.end_time + ":00",
            service: this.state.service
        }
        console.log("start date " + this.state.start_date);
        console.log("start time " + this.state.start_time);
        console.log("end time " + this.state.end_time);
        console.log(newbookings);
        CreateBooking.createBooking(newbookings).then(res => {
            alert("Booking successful");
            this.props.history.push("/currentbookings");
        });
    }

    componentDidMount(){
        
        Services.getAllServices().then((res) => {
            this.setState({ allservices: res.data});
            console.log(res.data);
        });

    }

    render() {

        return (
            <React.Fragment>
                <CustomerDashboard/>
                <div className="container">
                    <div className="row">
                        <div className="col-md-8 m-auto">
                            <h5 className="display-4 text-center pb-5">Create New Booking</h5>
                            
                            <form onSubmit={this.onSubmit} >

                                <h5>Select Service and Worker</h5>
                                <hr/>
                                <h6>Service</h6>
                                <div className="form-group">
                                    <select id="inputState" className="form-control" name="service" value= {this.state.service} onChange = {this.handleServiceChange}  required>
                                        <option value="unknown" defaultValue>Choose Service</option>
                                        {
                                            this.state.allservices.map(
                                                allservices => 
                                                <option className="service" key={allservices} value={allservices}>{allservices}</option>
                                            )
                                        }
                                    </select>
                                </div>

                                
                                <h6>Staff</h6>
                                
                                <div className="form-group">
                                    <select id="inputState" className="form-control" name="worker" value= {this.state.worker} onChange = {this.handleWorkerSelection}  required>
                                        <option value="unknown" defaultValue>Choose Staff</option>
                                        {
                                            this.state.allworker.map(
                                                allworker => 
                                                <option className="worker" key={allworker.id} value={allworker.id}> {allworker.fName}</option>
                                            )
                                        }
                                    </select>
                                </div>

                                <h6>Sessions</h6>

                                <div className="form-group">
                                    <select id="inputState" className="form-control" name="start_date" value= {this.state.start_date} onChange = {this.onChange} required>
                                        <option value="unknown" defaultValue>Choose Date</option>
                                        {
                                            this.state.availableSessions.map(
                                                availableSessions => 
                                                <option className="sessionDate" key={availableSessions.id} value={availableSessions.date}> {availableSessions.date}</option>
                                            )
                                        }
                                    </select>
                                </div>

                                <div className="form-group">
                                    <select id="inputState" className="form-control" name="start_time" value= {this.state.start_time} onChange = {this.onChange} required>
                                        <option value="unknown" defaultValue>Choose Start Time</option>
                                        {
                                            this.state.availableSessions.map(
                                                availableSessions => 
                                                <option className="sessionStart" key={availableSessions.id} value={availableSessions.startTime}> {availableSessions.startTime}</option>
                                            )
                                        }
                                    </select>
                                </div>

                                <div className="form-group">
                                    <select id="inputState" className="form-control" name="end_time" value= {this.state.end_time} onChange = {this.onChange} required>
                                        <option value="unknown" defaultValue>Choose End Time</option>
                                        {
                                            this.state.availableSessions.map(
                                                availableSessions => 
                                                <option key={availableSessions.id} value={availableSessions.endTime}> {availableSessions.endTime}</option>
                                            )
                                        }
                                    </select>
                                </div>

                                <input type="submit" className="btn btn-primary btn-block mt-4" />
                            </form>
                        </div>
                    </div>
                </div>

            </React.Fragment>
            
        )
    }
}
export default NewBookings;